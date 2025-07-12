const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp();

// Helper: Send FCM
async function sendNotification(token, payload) {
  await admin.messaging().send({
    token,
    data: payload // Use data payload for full control
  });
}

// 1. New Chat Message Notification
exports.onNewMessage = functions.firestore
  .document('chatChannels/{channelId}/messages/{messageId}')
  .onCreate(async (snap, context) => {
    const message = snap.data();
    const channelRef = admin.firestore().collection('chatChannels').doc(context.params.channelId);
    const channelDoc = await channelRef.get();
    if (!channelDoc.exists) return;

    const channel = channelDoc.data();
    const recipientId = (channel.user1Id === message.senderId) ? channel.user2Id : channel.user1Id;

    // Get recipient's FCM token
    const userDoc = await admin.firestore().collection('users').doc(recipientId).get();
    const token = userDoc.get('fcmToken');
    if (!token) return;

    await sendNotification(token, {
      type: 'chat_message',
      title: 'New Message',
      body: message.text,
      sender: message.senderId,
      channelId: context.params.channelId
    });
  });

// 2. New Bid Notification
exports.onNewBid = functions.firestore
  .document('bids/{bidId}')
  .onCreate(async (snap, context) => {
    const bid = snap.data();
    const assignmentDoc = await admin.firestore().collection('assignments').doc(bid.assignmentId).get();
    if (!assignmentDoc.exists) return;
    const assignment = assignmentDoc.data();

    // Notify the assignment poster
    const userDoc = await admin.firestore().collection('users').doc(assignment.createdBy).get();
    const token = userDoc.get('fcmToken');
    if (!token) return;

    await sendNotification(token, {
      type: 'new_bid',
      title: 'New Bid Received',
      body: `${bid.bidderName} placed a bid on your assignment "${assignment.title}"`,
      assignmentId: bid.assignmentId,
      bidId: context.params.bidId
    });
  });

// 3. Bid Accepted Notification
exports.onBidAccepted = functions.firestore
  .document('bids/{bidId}')
  .onUpdate(async (change, context) => {
    const before = change.before.data();
    const after = change.after.data();
    if (before.status !== 'accepted' && after.status === 'accepted') {
      // Notify the bidder
      const userDoc = await admin.firestore().collection('users').doc(after.bidderId).get();
      const token = userDoc.get('fcmToken');
      if (!token) return;

      await sendNotification(token, {
        type: 'bid_accepted',
        title: 'Bid Accepted!',
        body: `Your bid for "${after.assignmentId}" was accepted!`,
        assignmentId: after.assignmentId,
        bidId: context.params.bidId
      });
    }
  });

// 4. User Rated Notification
exports.onAssignmentRated = functions.firestore
  .document('assignments/{assignmentId}')
  .onUpdate(async (change, context) => {
    const before = change.before.data();
    const after = change.after.data();
    if (
      !before.bidderRating && after.bidderRating && after.acceptedBidderId
    ) {
      // Notify the accepted bidder
      const userDoc = await admin.firestore().collection('users').doc(after.acceptedBidderId).get();
      const token = userDoc.get('fcmToken');
      if (!token) return;

      await sendNotification(token, {
        type: 'user_rated',
        title: 'You\'ve been rated!',
        body: `You received a rating of ${after.bidderRating} stars.`,
        assignmentId: context.params.assignmentId
      });
    }
  });
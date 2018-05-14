'use strict';

const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp(functions.config().firebase);

exports.sendNotification = functions.database.ref('/messages/{messageId}/{pushId}')
    .onWrite(event => {
        const message = event.data.current.val();
        const messageThreadId = event.params.messageId;
        const senderUid = message.sender;
        const receiverUid = message.receiver;
        const promises = [];

        console.log("message thread id is: "+messageThreadId);

        if (senderUid === receiverUid || senderUid ==="System") {
            //if sender is receiver, don't send notification
            //promises.push(event.data.current.ref.remove());
            return Promise.all(promises);
        }

        const getInstanceIdPromise = admin.database().ref(`/users/${receiverUid}/instanceId`).once('value');
        const getSenderUidPromise = admin.auth().getUser(senderUid);

        return Promise.all([getInstanceIdPromise, getSenderUidPromise])
        .then((results) => {
            const instanceId = results[0].val();
            const sender = results[1];
            console.log('notifying ' + receiverUid + ' about ' + message.message + ' from ' + senderUid);


            //Notification details + data
            const payload = {
                notification: {
                    title: "RE :"+message.chat_title,
                    body: message.message,
                    click_action:"ChatMessageActivity",
                },
                data:{
                    message_id: messageThreadId,
                    chat_title: message.chat_title,
                    receiver_chat: senderUid,
                }
            };

           return admin.messaging().sendToDevice(instanceId, payload);
           }).then((response) => {
                 // For each message check if there was an error.
                 const tokensToRemove = [];
                 response.results.forEach((result, index) => {
                   const error = result.error;
                   if (error) {
                     console.error('Failure sending notification to', tokens[index], error);
                     // Cleanup the tokens who are not registered anymore.
                     if (error.code === 'messaging/invalid-registration-token' || error.code === 'messaging/registration-token-not-registered') {
                       //tokensToRemove.push(tokensSnapshot.ref.child(tokens[index]).remove());
                     }
                   }
             });
             return Promise.all(tokensToRemove);
        });
  });

  exports.sendNotificationUponABookPosted = functions.database.ref('/wish_list/{book_isbn}/{post_id}')
    .onWrite(event=>{
        const post_id = event.params.post_id;
        const book_isbn = event.params.book_isbn;
        const this_post = event.data.current.val();
        const promises = [];

        console.log("CHecking "+ post_id +" and "+book_isbn);

        if(post_id==="first"){
            return Promise.all(promises);

        }

        const topic = book_isbn;
        const payload = {
            notification: {
                title: this_post.book_title+ " has become available",
                body: "Tap here to go view in details",
                click_action:"ItemDetailsActivity",
            },
            data:{
             post_id: post_id,
         }

        };

        return admin.messaging().sendToTopic(topic, payload);


    });


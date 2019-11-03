const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp();
// Create and Deploy Your First Cloud Functions
// https://firebase.google.com/docs/functions/write-firebase-functions

exports.helloWorld = functions.https.onRequest((request, response) => {
 	response.send("Hello from Firebase!");
});

exports.sendNotification = functions.https.onRequest((request, response) => {
	var registrationToken = 'cEfk5c77HpM:APA91bHM_XBhr75aOdz9hyPJ2z-XEMhd7WnKIPmcWIrS-51B_i0ZXCY3iKn2ZUldpWkFgRhU9Kh4PDBUyxyFzInKcIT5BR8RIllLHLWMUjiProOw-Rvkr3OZjX5OsZvbbVUQ2DpsMsU7';
	
	var message = {
		notification: {
			title: 'Saree3 ',
			body: 'Tap here to open Saree3!'
		},
		data: {
			score: '850',
			time: '2:45'
		}
	};

	// Send a message to the device corresponding to the provided
	// registration token.
	admin.messaging().sendToDevice(registrationToken, message)
	  .then((response) => {
	    // Response is a message ID string.
	    console.log('Successfully sent message:', response);
	    return true;
	  })
	  .catch((error) => {
	    console.log('Error sending message:', error);
	    return true;
	  });
 	response.send("sending Notification!");
});
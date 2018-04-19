
//database url


//var url = 'mongodb://127.0.0.1:50432/db';

var url = 'mongodb://127.0.0.1:23406/db';
var collections = ['rides', 'users', 'RideHistory', 'FullRidesRecorded'];

var mongojs = require('mongojs');
var assert = require('assert');

console.log("MongoDB is active.");


module.exports = function () {
    var mongodb = mongojs(url, collections); //creation of the mongo connection

    mongodb.on('error', function (err) {
	console.log('database error', err)
    })

    mongodb.on('connect', function () {
	console.log('database connected DulBik')
    })

    /** ********************************************************************
     * printDatabase - Prints the whole collection, for debugging purposes.
     * @param collectionName - the name of the collection
     * @param callback - need to provide a function to return the data
     */
    printDatabase = function (collectionName, callback) {

	//
	// Collection look ups with find return a
	// MongoDB 'cursor'. More info can be found here
	// https://docs.mongodb.com/v3.2/reference/glossary/#term-cursor
	//

	var cursor = mongodb.collection(collectionName).find(function (err, docs) {

	    if (err || !docs) {
		console.log("Cannot print database or database is empty\n");
	    }
	    else {
		//console.log(collectionName, docs);

		callback(docs);
	    }
	});

    };

    printRides = function (colName, callback) {
	var cursor = mongodb.collection(colName).find(function (err, docs) {
	    if (err || !docs) {
		console.log("database empty\n");
	    }
	    else {
		callback(docs);
	    }
	});
    };

    insertRoute = function (routeData) {
	mongodb.collection('RideHistory').save(
	    { point: routeData }, function (err, result) {
		if (err || !result) console.log("point not saved");
		else console.log("point entered into RideHistory");
	    });
    };

    insertFullRide = function (fullRide) {
	mongodb.collection('FullRidesRecorded').save(
	    { ride: fullRide }, function (err, result) {
		if (err || !result) console.log("ride not saved");
		else console.log("ride loged in FullRidesRecord");
	    });
    };

    insertLocalLeaderboard = function (pos, stats) {
	var posInt = pos.pos //pos is an array of key value pairs, pos is the key value for the position
	if (posInt >= 1 && posInt <= 3) {
	    mongodb.collection('localLeaderboard').update(
		{ position: pos }, //'find' query
		{
		    position: pos,	//update these (or insert)
		    date: stats.date,
		    time: stats.time,
		    distance: stats.distance,
		    name: stats.name
		},
		{ upsert: true } //insert if not found
	    )
	}
	else {
	    console.log('invalid pos passed to insertLocalLeaderboard!');
	}
    }

    insertGlobalLeaderboard = function (pos, stats) {
	var posInt = pos.pos //pos is an array of key value pairs, pos is the key value for the position
	if (posInt >= 1 && posInt <= 3) {
	    mongodb.collection('globalLeaderboard').update(
		{ position: pos }, //'find' query
		{
		    position: pos,	//update these (or insert)
		    date: stats.date,
		    time: stats.time,
		    distance: stats.distance,
		    name: stats.name
		},
		{ upsert: true } //insert if not found
	    )
	}
	else {
	    console.log('invalid pos passed to insertGlobalLeaderboard!');
	}
    }

    insertLatLng = function (LatLng) {
	mongodb.collection('FullLatLngsRecorded').save(
	    { latlng: LatLng }, function (err, result) {
		if (err || !result) console.log("latlng not saved")
		else console.log("latlng loged in DB")})}

    loginAttempt = function (user, callback) {
	mongodb.collection('users').find({name:user.name,pass:user.pass}, (err,docs)=>{
	    console.log("login attempt")
	    if(err) callback(err, null)
	    else if(docs.length == 0) callback("user not found", null)
	    else if(docs.length > 1 ) callback("username collision", null)
	    else {
		callback(null, docs[0]._id)
	    }
	})
    }

    insertUser = function (user, callback){
	mongodb.collection('users').find({name:user.name}, (err,docs)=>{
	    console.log("insertUser")
	    if(err) callback("database error on find", null)
	    else if(docs.length > 0) callback("user already exists", null)
	    else mongodb.collection('users').insert(user, (err, docs)=>{
		if(err) callback("database error on insert", null)
		else callback(null, "Account created sucessfullly")})})}

    getFriends = (username, callback) =>{
	mongodb.collection('users').find({name:username}, (err, docs)=>{
	    if(err) callback(err, null)
	    else if(docs.length != 1) callback("user does not exist")
	    else callback(null, docs[0].friends)
	})}

    addFriend = (obj, callback) =>{
	mongodb.collection('users').find({_id:obj.user}, (err,docs)=>{
	    if(err) callback(err, null)
	    else if(docs.length != 1) callback("you do not exist", null)
	    else if(obj.friend in docs[0].friends) callback("friend already added", null)
	    else mongodb.collection("users").find({name:obj.friend}, (err, docs) =>{
		if(err) callback(err, null)
		else if(docs.length != 1) callback(obj.freind + " does not exist", null)
		else mongodb.collection("users").update({_id:obj.user},
						   {$push: {friends:obj.friend}},
						   (err, docs) =>{
		    if(err) callback(err, null)
		    else callback(null, "success")
						   })
	    })
	})
    }
    
    removeFriend = (obj, callback) =>{
	mongodb.collection('users').find({_id:obj.user}, (err,docs)=>{
	    if(err) callback(err, null)
	    else if(docs.length != 1) callback("you do not exist", null)
	    else if(!obj.friend in docs[0].friends) callback("friend not in list", null)
	    else mongodb.collection("users").find({name:obj.friend}, (err, docs) =>{
		if(err) callback(err, null)
		else if(docs.length != 1) callback(obj.freind + " does not exist", null)
		else mongodb.collection("users").update({_id:obj.user},
						   {$pull: {friends:obj.friend}},
						   (err, docs) =>{
		    if(err) callback(err, null)
		    else callback(null, "success")
						   })
	    })
	})
    }
    
    
    printUsers = function (callback) {
	var cursor = mongodb.collection('users').find({},function (err, docs) {
	    if (err || !docs) {
		console.log("Cannot print database or database is empty\n");}
	    else {callback(docs);}});};

    printLocalLeaderboard = function (collectionName, callback) {

	var cursor = mongodb.collection(collectionName).find(function (err, docs) {
	    if (err || !docs) {
		console.log("Cannot print database or database is empty\n");
	    }
	    else {
		callback(docs);
	    }
	});

    };

    printGlobalLeaderboard = function (collectionName, callback) {

	var cursor = mongodb.collection(collectionName).find(function (err, docs) {
	    if (err || !docs) {
		console.log("Cannot print database or database is empty\n");
	    }
	    else {
		callback(docs);
	    }
	});

    };

    insertPicture = function (pic) {
        mongodb.collection('PicturesSaved').save({ pictures: pic }, function (err, result) {
            if (err || !result)
                console.log("Picture not saved");
            else
                console.log("picture saves in picture DB")
        });
    };

    getPicture = function (pic, callback) {
        mongodb.collection('PicturesSaved').find({description:pic.description}, function (err,docs) {
            if(err) {
                callback(err, null);
            }
            else {
                callback(null, docs.picture);
            }
        });
    };

    printPictures = function (collectionName, callback) {
       var cursor = mongodb.collection(collectionName).find(function (err, docs) {
           if (err || !docs) {
               console.log("Cannot print database or database is empty\n");
           }
           else {
               callback(docs);
           }
       });
   };

    deleteAll = function (colName, callback) {
	mongodb.collection(colName).remove({});
	callback(true);
    };

    return mongodb;

};

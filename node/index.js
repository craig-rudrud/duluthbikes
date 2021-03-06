/*
 * Our node server index.js file for use with our
 * duluthbikes app and the corresponding
 * dashboard
 *
 */

// Body Parser for icoming http requests
var bodyParser = require('body-parser');
// require express
var express = require('express');
var session = require('express-session');
// express framework for handling http requests
var app = express();
var http = require('http').Server(app);
var io = require('socket.io')(http);


/*
 * varaible area for any required varaibles we might use
 */

var routeHistory = [];

// setting the port for the app system to use
app.set("port",23405);

// this section tells the body parser what type of data to expect
// for now it is mainly json
// Set limits in order to send images.
app.use(bodyParser.urlencoded({
    limit: '50mb',
    extended: true
}));

app.use(bodyParser.json({limit: '50mb'}));

app.use(session({secret: "TODO: figure out what a secret is"}));


//Include all files in the public folder to serve to the website
app.use(express.static('public'));

//********MangoDB*******
// Connect to the mongo module
var mongodb = require('./mongoDB.js')();
console.log(mongodb);

app.get('/isLoggedIn', (req,res) =>{
    console.log(req.session)
    if(req.session.login) res.send("true")
    else res.send("false")

})

app.get('/heatmapfiles',function(req,res){
    res.sendFile(__dirname + '/public/node_modules/heatmap.js/build/heatmap.js');
});

app.get('/heatmapfilesgmaps',function(req,res){
    res.sendFile(__dirname + '/public/node_modules/heatmap.js/plugins/gmaps-heatmap/gmaps-heatmap.js');
});

app.get('/fullRide',function(req,res){
    var rides = printRides('FullRidesRecorded',function(result){
	res.write('<HTML><head><title>Duluth Bikes DashBoard</title></head><BODY>'
		  +'<H1>Full Rides.</H1>');
	result.reverse();
	res.write(JSON.stringify(result));
	res.send();
    });
    console.log('full ride request');
});

app.get('/fulllatlng',function(req,res){
    if(!req.session.login) return res.sendStatus(403);
    var rides = printRides('FullLatLngsRecorded',function(result){
        res.write('<HTML><head><title>Duluth Bikes DashBoard</title></head><BODY>'
                  +'<H1>Full Rides.</H1>');
        result.reverse();
        res.write(JSON.stringify(result));
        res.send();
    });
    console.log('full ride request');
});

// this next section is for our GET, POST, PUT, DELETE routes
// the first one is the default dashboard route
//
app.get('/raw', function(request, response) {
    if(!req.session.login) return res.sendStatus(403);
    //when using Mongo
    var str = printDatabase('RideHistory', function(result) {
	response.write('<HTML><head><title>Duluth Bikes DashBoard</title></head><BODY>'
		       + '<H1>Duluth Bikes Route Data.</H1>')
	result.reverse();
	response.write(JSON.stringify(result,['point','lat','lng'],'\n') + '</BODY></HTML>');
	response.send();
    });

    console.log('DashBoard request received!');
});

app.get('/rides',function(request,response){
    response.sendFile(__dirname +'/public/ride.html');
    printRides('FullRidesRecorded',function(doc){
        io.emit('FullRidesRecorded',doc);
    });
});

app.post('/friends',(req,res) => {
    if(!req.body.name) return res.sendStatus(400)
    getFriends(req.body.name, (err, doc) =>{
	if(err) res.send(err)
	else res.send(doc)
    })
})

app.post('/addFriend', (req,res) =>{
    if(!req.body.name || !req.session.uid) return res.sendStatus(400)
    obj = {user:mongodb.ObjectId(req.session.uid),
	   friend: req.body.name}
    addFriend(obj, (err, docs)=>{
	if(err) res.send(err)
	else res.sendStatus(200)
    })
})

app.post('/removeFriend', (req,res) =>{
    if(!req.body.name || !req.session.uid) return res.sendStatus(400)
    obj = {user:mongodb.ObjectId(req.session.uid),
	   friend: req.body.name}
    removeFriend(obj, (err, docs)=>{
	if(err) res.send(err)
	else res.sendStatus(200)
    })
})


//this exists because we don't have cookies working in app
app.post('/addfriendbyuser', (req,res) =>{
    //req.body.uid is acutally a name, sorry
    if(!req.body.name || !req.body.uid) return res.sendStatus(400)
    obj = {user:req.body.uid,
	   friend: req.body.name}
    addFriendByUser(obj, (err, docs)=>{
	if(err) res.send(err)
	else res.sendStatus(200);
    })
    // //adds to both friends list
    // obj = {user:req.body.name,
    // 	   friend: req.body.uid}
    // addFriendByUser(obj, (err, docs)=>{
    // 	if(err) res.write("\n"+err)
    // 	else res.write("OK")
    // })
    // res.send();
})
//This is bad because we present tomorrow
app.post('/removefriendbyuser', (req,res) =>{
    if(!req.body.name || !req.body.uid) return res.sendStatus(400)
    obj = {user:req.body.uid,
	   friend: req.body.name}
    removeFriendByUser(obj, (err, docs)=>{
	if(err) res.send(err)
	else res.sendStatus(200)
    })
    // //removes from both friends list
    // obj = {user:req.body.name,
    // 	   friend: req.body.uid}
    // addFriendByUser(obj, (err, docs)=>{
    // 	if(err) res.write("\n"+err)
    // 	else res.write("OK")
    // })
    // res.send();
})



app.get('/maps',function(req,res){
    res.sendFile(__dirname + '/public/maps.html');
    printRides('FullRidesRecorded',function(doc){
	io.emit('FullRidesRecorded',doc);
    });
});

app.get('/',function(req,res){
    res.sendFile(__dirname + '/public/duluthBikesBootstrap.html');
});


app.get('/clickPlaces', function (req, res) {
   var users = printDatabase('clicks',function(result){
	res.write(JSON.stringify(result));
	res.send();
    });
    console.log('get clickPlaces');
});

app.get('/deleteClicks/:placeName', function (req, res) {
    console.log('deleted clicks for Places '+req.params.placeName);

    deleteClicks(req.params.placeName, function(result) {
	if(result){
	    console.log("delete clicks");}
	else {
	    console.log("Did not work");}
    });
    var jsonResponse = {
        id: '123', status: 'updated'
    };
    res.json(jsonResponse);


});

app.post('/postClickPlaces', function(request,response) {
    if (!request.body) return response.sendStatus(400);
    var clickData = {
	'placeName':request.body.placeName,
	'clickTimes':request.body.clickTimes
    }
    
    insertClickPlaces(clickData);
    console.log("placeName: " + request.body.placeName);
    console.log("clickTimes: " + request.body.clickTimes);
    console.log('Post Request: postClickPlaces');
    response.sendStatus(200);
});


app.post('/postlocalleaderboard', function(request,response) {

    if (!request.body) return response.sendStatus(400);

    var position = {
	'pos':request.body.pos
    }
    var statData = {
	'date':request.body.date,
	'distance':request.body.distance,
	'time':request.body.time,
	'name':request.body.name
    }
    insertLocalLeaderboard(position,statData);
    console.log('Post Request: postlocalleaderboard');
    response.sendStatus(200);
})

app.get('/usernames', function(req,res){
    var users = printUsers(function(result){
        res.write(JSON.stringify(result));
        res.send();
    });
    console.log('users request');
});

app.get('/localleaderboard', function(req, res) {
    var users = printLocalLeaderboard('localLeaderboard',function(result){
	res.write(JSON.stringify(result));
	res.send();
    });
    console.log('local leaderboard request');
});

app.get('/globalleaderboard', function(req, res) {
    var users = printLocalLeaderboard('globalLeaderboard',function(result){
	res.write(JSON.stringify(result));
	res.send();
    });
    console.log('global leaderboard request');
});

app.get('/logout', (req, res)=>{
    if(!req.session.login) return res.send("You must log in to logout")
    req.session.login = false
    delete req.session.uid
    res.send ("logout");
});


app.post('/postlocalleaderboard', function(request,response) {
    if(!req.session) return res.sendStatus(403)
    if (!request.body) response.sendStatus(400);

    var position = {
	'pos':request.body.pos
    }
    var statData = {
	'date':request.body.date,
	'distance':request.body.distance,
	'time':request.body.time,
	'name':request.body.name
    }
    insertLocalLeaderboard(position,statData);
    console.log('Post Request: postlocalleaderboard');
    response.sendStatus(200);
});

app.post('/postgloballeaderboard', function(request,response) {
    if (!request.body) return response.sendStatus(400);
    if(!req.session.login) res.sendStatus(403)

    var position = {
	'pos':request.body.pos
    }
    var statData = {
	'date':request.body.date,
	'distance':request.body.distance,
	'time':request.body.time,
	'name':request.body.name
    }
    insertGlobalLeaderboard(position,statData);
    console.log('Post Request: postgloballeaderboard');
    response.sendStatus(200);
});

app.post('/postroute', function(request, response) {
    if(!req.session.login) return res.sendStatus(403)
    if (!request.body) response.sendStatus(400);

    var routeData = {'lat':request.body.lat,
		     'lng':request.body.lang};

    var date = {'Date':request.body.time};

    //Mongo
    insertRoute(routeData);

    console.log('Post Request: postroute');

    response.sendStatus(200);
});

app.post('/postfinish',function(req,res){
    if(!req.session.login) return res.send(403)
    if(!req.body) return res.sendStatus(400);
    var arr = [];
    arr = req.body.ride;
    insertFullRide(arr);
    if(req.body.heat){
	var latlng = [];
	latlng = req.body.heat;
	insertLatLng(latlng);
	io.emit('FullRidesRecorded',doc);
    }
    console.log('Post Full Ride');
    res.sendStatus(200);
});



app.post('/loginAttempt', function(req,res){
    //if(req.session.login) return res.send("you must logout before you log in")
    if(!req.body.name || !req.body.pass) return res.sendStatus(400)
    var userObj = { 'name':req.body.name, 'pass':req.body.pass}
    loginAttempt(userObj, (err, uid) =>{
	if(err) res.send(err)
	else {
	    req.session.login = true
	    req.session.uid = uid
	    console.log(req.session)
	    res.sendStatus(200)
	}
    })
})

app.post('/newAccount', function(req,res){
    if(!req.body.name || !req.body.pass) return res.sendStatus(400);
    var userObj = { 'name':req.body.name,
		    'pass':req.body.pass,
		    'email':req.body.email,
		    'friends':[]}
    insertUser(userObj, (err, docs)=>{
	if(err) res.send(err)
	else res.send(docs)})})

app.post('/postpicture', function(req,res){
    if(!req.session.login) return res.sendStatus(403)
    //if(!req.body.userName || !req.body.passWord) return res.sendStatus(400);
    var picObj = {
        'location':req.body.loc,
	'description':req.body.description,
	'picture':req.body.picture };
    insertPicture(picObj);
    console.log('Post Picture');
    res.send();
});

app.get('/getpicture', function(req, res){
    if(!req.session.login) return res.sendStatus(403)
    if(!req.body.description) {
        return res.sendStatus(400)
    }

    var picObj = {'description': req.body.description}
    getPicture(picObj, (err, docs)=>{
        if(err) res.send(err)
        else res.send(docs)
    })
});

app.get('/pictures',function(req,res){
    // 1.// THE FOLLOWING IS FOR ACCESSING DB. ( CURRENTLY DOES NOT ACCESS - PICS HARDCODED.)
    res.sendFile(__dirname +'/public/threepics.html'); // Will try and use if we can use Canvas element - HTML5
    printPictures('PicturesSaved',function(doc){
	io.emit('PicturesSaved',doc);
    });

    // 2.// THE FOLLOWING WILL PRINT THE RAW PICTURE DATA STORED IN DB
    //var pics = printPictures('PicturesSaved',function(result){
    //    res.write('<HTML><head><title>Duluth Bikes DashBoard</title></head><BODY>'
    //        +'<H1>Pictures.</H1>');
    //        res.write(JSON.stringify(result));
    //        res.send();
    //    });
    console.log('picture request');
});


io.on('connection',function(socket){
    console.log('a socket io connection');

    printRides('FullRidesRecorded',function(doc){
	socket.emit('FullRidesRecorded',doc);
    });
});

function convertBase64ToImage(){

}



// this last section is to start the app and start listening on
// the given port for requests
//



http.listen(app.get("port"),function(){
    console.log('duluth bikes node listening on port:',app.get("port"));
});

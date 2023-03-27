// set up Express
var express = require('express');
var app = express();

// set up BodyParser
var bodyParser = require('body-parser');
app.use(bodyParser.urlencoded({ extended: true }));

// import the Post class from Post.js
var Post = require('./Post.js');
// import the User class from User.js
var User = require('./User.js');

/***************************************/

// endpoint for creating a new post
// this is the action of "postform.html"
app.use('/createPost', (req, res) => {

	tags = req.body.source
	console.log(tags)
	// construct the Post from the form data which is in the request body
	var newPost = new Post ({
		_id: mongoose.Types.ObjectId().toHexString(),
		user: req.body.user,
		price: req.body.price,
		desc: req.body.desc,
		available: req.body.status

	    });

	// save the person to the database
	newPost.save( (err) => { 
		if (err) {
		    res.type('html').status(200);
		    res.write('uh oh: ' + err);
		    console.log(err);
		    res.end();
		}
		else {
		    // display the "successfull created" message
		    res.send('successfully added ' + newPost.user + '\'s post to the database');
		}
	    } ); 
    }
    );


// endpoint for creating a new user
// this is the action of "personform.html"
app.use('/createUser', (req, res) => {
	// construct the Post from the form data which is in the request body
	var newUser = new User ({
		name: req.body.name,
		id: req.body.id,
		email: req.body.email,
		bio: req.body.bio
	    });

	// save the person to the database
	newUser.save( (err) => { 
		if (err) {
		    res.type('html').status(200);
		    res.write('uh oh: ' + err);
		    console.log(err);
		    res.end();
		}
		else {
		    // display the "successfull created" message
		    res.send('successfully added new user ' + newUser.name + ' to the database.');
		}
	    } ); 
    }
    );


// endpoint for showing all the posts
app.use('/allPosts', (req, res) => {
	Post.find({}, (err, post) => {
		if (err) {
		    res.type('html').status(400);
		    console.log('uh oh' + err);
			res.json({'status': 'error'});
		    res.write(err);
		}
		else if (!post || post.length == 0){
			res.json({'status' : 'no posts yet'});
		} else {
			res.type('html').status(400);
			var allRecords = [];
			for(let i = 0; i < post.length; i++)
			{
				allRecords.push({
					'user' : post[i].user,
					'price' : post[i].price,
					'desc' : post[i].desc,
					'available' : post[i].status,
				});
			}
			res.type('html').status(200);
			res.json( {
				'entries': allRecords,
				'status' : 'successful'
			});
		}
	}).sort({ '_id': 'asc' }); // this sorts them BEFORE rendering the results
});

// endpoint for showing all the uses
app.use('/allUsers', (req, res) => {
	Post.find({}, (err, post) => {
		if (err) {
		    res.type('html').status(400);
		    console.log('uh oh' + err);
			res.json({'status': 'error'});
		    res.write(err);
		}
		else if (!post || post.length == 0){
			res.json({'status' : 'no users yet'});
		} else {
			res.type('html').status(400);
			var allRecords = [];
			for(let i = 0; i < post.length; i++)
			{
				allRecords.push({
					'name' : post[i].name,
					'username' : post[i].id,
					'email' : post[i].email,
					'bio' : post[i].bio,
				});
			}
			res.type('html').status(200);
			res.json( {
				'entries': allRecords,
				'status' : 'successful'
			});
		}
	}).sort({ 'name': 'asc' }); // this sorts them BEFORE rendering the results
});

// endpoint for showing a single user
app.use('/singleUser', (req, res) => {
	// construct the query object
	var filter = {};
	if (req.body.user && (req.body.user.length > 0)) {
	    // if there's a name in the query parameter, use it here
	    filter = { "id" : req.body.user };
	} else {
		res.json({"status" : "No user was specified."});
		return;
	}

	User.findOne(filter, (err, user) => {
		if (err) {
		    res.type('html').status(400);
		    console.log('uh oh' + err);
			res.json({'status': 'error'});
		    res.write(err);
		}
		else if (!user || user.length == 0) {
			res.json({'status' : 'User not found'});
		} else {
			res.type('html').status(400);
			user = { 'name' : user.name,
					'id' : user.id, 
					'email' : user.email,
					'bio' : user.bio };
			res.type('html').status(200);
			res.json({
				'user': user
			});
		}
	});
});

// endpoint to edit a post
app.use('/edit', (req, res) => {
	if (!req.body.user || !req.body.property || !req.body.newValue) {
		res.json({'status': 'Missing data. Please provide username, property, and new value.'});
		return;
	}
	var filter = { 'user' : req.body.user };
	var property = req.body.property;
	var newValue = req.body.newValue;
	var jsonObj = {};
	jsonObj[property] = newValue
	var action = { '$set' : jsonObj };

	Post.findOneAndUpdate( filter, action, (err, orig) => {
		if (err) {
		    res.type('html').status(400);
		    console.log('uh oh' + err);
			res.json({'status': 'error'});
		    res.write(err);
		} else if (!orig) {
			res.json({'status': 'No post matched that data.'});
		} else {
			res.json({'status': 'Success! Post updated.'});
		}
	})

}); 

//endpoint for viewing a post 
// view post by post id 
app.use('/viewPost', (req, res) => {
    var filter = {};
	if (req.body.posts && (req.body.posts.length > 0)) {
	    // if there's a name in the query parameter, use it here
	    filter = { "id" : req.body.posts };
	} else {
		res.json({"status" : "No post was specified."});
		return;
	}

    Post.findOne(filter, (err, posts) => {
		if (err) {
		    res.type('html').status(400);
		    console.log('uh oh' + err);
			res.json({'status': 'error'});
		    res.write(err);
		}
		else if (!posts || posts.length == 0) {
			res.json({'status' : 'User not found'});
		} else {
			res.type('html').status(400);
			posts = { 'user' : posts.user,
					'price' : posts.price, 
					'description' : posts.desc,
					'status' : posts.status };
			res.type('html').status(200);
			res.json({
				'post': posts
			});
		}
	});
});




/*************************************************/

app.use('/public', express.static('public'));

app.use('/', (req, res) => { res.redirect('/public/homepage.html'); } );

app.listen(3000,  () => {
	console.log('Listening on port 3000');
    });

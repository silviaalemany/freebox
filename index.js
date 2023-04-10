// set up Express
var express = require('express');
var app = express();
// set up BodyParser
var bodyParser = require('body-parser');
app.use(bodyParser.urlencoded({ extended: true }));
var mongoose = require('mongoose');
// import the Post class from Post.js
var Post = require('./Post.js');
// import the User class from User.js
var User = require('./User.js');

/***************************************/

// endpoint for creating a new post
// this is the action of "postform.html"
app.use('/createPost', (req, res) => {
	if (!req.body.user || !req.body.price) {
		res.json({'status': 'Missing data. Please provide username of post creator and price.'});
		return;
	}
	filter = {"id" : req.body.user};
	User.find(filter, (err, usr) => {
		if (err) {
		    res.type('html').status(400);
		    console.log('uh oh' + err);
			res.json({'status': 'error'});
		    res.write(err);
			return;
		}
		else if (!usr || usr.length == 0){
			res.json({'status' : 'user does not exist.'});
			return;
		}
	})
	tagsInput = req.body.source;
	
	// construct the Post from the form data which is in the request body
	var newPost = new Post ({
		_id: mongoose.Types.ObjectId().toHexString(),
		user: req.body.user,
		price: req.body.price,
		desc: req.body.desc,
		status: true,
		tags: tagsInput
	});
		//available: req.body.status,
		//tags: req.body.source
	   // });

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
		    res.send('successfully added ' + newPost.user + '\'s post to the database.');
		}
	    } ); 
    }
    );

// endpoint for creating a new post for the app
// this is the action of "postform.html"
app.use('/createPostApp', (req, res) => {
	if (!req.query.user || isNaN(req.query.price)) {
		res.json({'message' : 'Missing data. Please provide username of post creator and price.'});
	}
	filter = {"id" : req.query.user};
	User.find(filter, (err, usr) => {
		if (err) {
		    console.log(err);
		    res.json({"message" : 'Could not find user.'});
		}
		else if (!usr || usr.length == 0){
			res.json({"message": 'User does not exist.'});

		}
	})
	
	// construct the Post from the form data which is in the request body
	var newPost = new Post ({
		_id: mongoose.Types.ObjectId().toHexString(),
		user: req.query.user,
		price: req.query.price,
		desc: req.query.desc,
		status: true,
	});

	newPost.save( (err) => { 
		if (err) {
			console.log(err);
		    res.json({'message' : 'Issue saving post.'});
		}
		else {
		    res.json({'message' : 'Successfully added ' + newPost.user + '\'s post to the database.'});
		}
	    } ); 
    }
    );



// endpoint for creating a new user
// this is the action of "personform.html"
app.use('/createUser', (req, res) => {
	if (!req.body.name || !req.body.id || !req.body.email) {
		res.json({'status': 'Missing data. Please provide name, username, and email.'});
		return;
	}
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
    });

app.use('/createUserApp', (req, res) => {
		if (!req.query.name || !req.query.id || !req.query.email) {
			res.json({'status': 'Missing data. Please provide name, username, and email.'});
			return;
		}
		// construct the Post from the form data which is in the request body
		var newUser = new User ({
			name: req.query.name,
			id: req.query.id,
			email: req.query.email,
			bio: req.query.bio
			});
	
		// save the person to the database
		newUser.save( (err) => { 
			if (err) {
				res.type('html').status(200);
				res.json({'status' : err});
				console.log(err);
				res.end();
			}
			else {
				// display the "successfull created" message
				res.send({'status' :'successfully added new user ' + newUser.name + ' to the database.'});
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
					'id' : post[i]._id,
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

// endpoint for showing all the users
app.use('/allUsers', (req, res) => {
	User.find({}, (err, usr) => {
		if (err) {
		    res.type('html').status(400);
		    console.log('uh oh' + err);
			res.json({'status': 'error'});
		    res.write(err);
		}
		else if (!usr || usr.length == 0){
			res.json({'status' : 'no users yet'});
		} else {
			res.type('html').status(400);
			var allRecords = [];
			for(let i = 0; i < usr.length; i++)
			{
				allRecords.push({
					'name' : usr[i].name,
					'username' : usr[i].id,
					'email' : usr[i].email,
					'bio' : usr[i].bio,
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
		console.log(filter);
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

// endpoint for showing a single user
app.use('/singleUserApp', (req, res) => {
	// construct the query object
	var filter = {};
	if (req.query.user && (req.query.user.length > 0)) {
	    // if there's a name in the query parameter, use it here
	    filter = { "id" : req.query.user };
	} else {
		res.json({"message" : "No user was specified."});
		return;
	}
	User.findOne(filter, (err, user) => {
		
		console.log(filter);
		if (err) {
		    res.type('html').status(400);
			console.log(err);
		    res.json({"message" : "Issue encountered finding user."});
		}
		else if (!user || user.length == 0) {
			res.json({"message" : "User not found."});
		} else {
			res.type('html').status(400);
			user = { 'name' : user.name,
					'id' : user.id, 
					'email' : user.email,
					'bio' : user.bio };
			res.type('html').status(200);
			filter = { "user" : user.id };
			var allRecords = [];
			Post.find(filter, (err, post) => {
				if (err) {
					console.log(err);
					res.json({
						"message": "success", 
						"user" : "User: " + user.name + ", ID: " + user.id + ", Email: " + user.email + ", Bio: " + user.bio, 
						"store" : allRecords});

				}
				else if (!post || post.length == 0){
					res.json({
						"message": "success", 
						"user" : "User: " + user.name + ", ID: " + user.id + ", Email: " + user.email + ", Bio: " + user.bio, 
						"store" : allRecords});
		
				} else {
					res.type('html').status(400);
					for(let i = 0; i < post.length; i++) {
						allRecords.push({
							'price' : post[i].price,
							'desc' : post[i].desc,
							'status' : post[i].status,
						});
				}
				res.type('html').status(200);
				res.json( {"message" :"success",
					"user" : "User: " + user.name + ", ID: " + user.id + ", Email: " + user.email + ", Bio: " + user.bio,
					"store" : allRecords
							
				});
			}
			});		
		}
	});
});


// endpoint to edit a post
app.use('/edit', (req, res) => {
	if (!req.body._id || !req.body.property) {
		res.json({'status': 'Missing data. Please provide post ID, property to edit, and new value.'});
		return;
	} 
	var property = req.body.property;
	var filter = { '_id' : req.body._id };
	var newValue = req.body.newValue;
	var jsonObj = {};
	jsonObj[property] = newValue
	// if user wants to replace required field with nothing, don't let them!
	if (!req.body.newValue && !(property == 'desc')) {
		res.json({'status': 'Cannot delete a required field.'});
		return;
	}
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

// endpoint to edit a user on the app 
app.use('/editUserApp', (req, res) => {
	if (!req.query.id || !req.query.property) {
		res.json({'status': 'Missing data. Please provide user, property to edit, and new value.'});
		return;
	} 
	var property = req.query.property;
	var filter = { 'id' : req.query.id };
	var newValue = req.query.newValue;
	var jsonObj = {};
	jsonObj[property] = newValue
	// if user wants to replace required field with nothing, don't let them!
	if (!req.query.newValue) {
		res.json({'status': 'Cannot delete a required field.'});
		return;
	}
	var action = { '$set' : jsonObj };

	User.findOneAndUpdate( filter, action, (err, orig) => {
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
	    filter = { "_id" : req.body.posts };
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
			res.json({'status' : 'Post not found'});
		} else {
			res.type('html').status(400);
			posts = { 
					'id': posts._id,
					'user' : posts.user,
					'price' : posts.price, 
					'description' : posts.desc,
					'status' : posts.status,
					'tags': posts.tags};
			res.type('html').status(200);
			res.json({
				'found': posts
			});
		}
	});
});

//endpoint for updating tags 
/** 
app.use('/addTag', (req, res) => {
	console.log(req.body.ID);
	console.log(req.body.source);

	if (!req.body.ID || !req.body.source) {
		res.json({'status': 'Missing data.'});
		return;
	}

	var filter = { 'id' : req.body.ID };
	var tags = req.body.source;

	var jsonObj = {};
	jsonObj['tags'] = tags
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





}); */

app.use('/deletePost', (req, res) => {
	console.log(req.body.posts);
	

	if (!req.body.posts) {
		res.json({'status': 'Missing data.'});
		return;
	}

	var filter = { 'id' : req.body.posts };
	

	Post.findOneAndDelete(filter,(err,post) => {
		if(err){
			res.type('html').status(400);
		    console.log('uh oh' + err);
			res.json({'status': 'error'});
		    res.write(err);
		}
		else if(!post){
			res.json({'status': 'No post matched that data.'});
		} 
		else {
			res.json({'status': 'Success! Post deleted.'});
		}

	});

}); 

app.use('/test', (req, res) => {
	var data = {'message' : 'It works!'};
	res.json(data);
}); 

/*************************************************/

app.use('/public', express.static('public'));

app.use('/', (req, res) => { res.redirect('/public/homepage.html'); } );

app.listen(3000,  () => {
	console.log('Listening on port 3000');
    });

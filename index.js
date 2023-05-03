// set up Express
var express = require('express');
var app = express();

// set up BodyParser
var bodyParser = require('body-parser');
const { json } = require('body-parser');
app.use(bodyParser.urlencoded({ extended: true }));

// require mongoose
var mongoose = require('mongoose');

// import the Post class from Post.js
var Post = require('./Post.js');

// import the User class from User.js
var User = require('./User.js');
var methodOverride  = require('method-override');

// Set the view engine
app.set('view engine', 'ejs');

app.get('/postSearch', (req, res) => {
	res.render('postSearch');
});

// Home page setup
app.get('/', (req, res) => {
	res.render('homePage');
});
app.post('/userSearch',  async function (req, res) {
	var searchResult = ` `;
	var data = {};
	var filter = {};
	if (req.body.user && (req.body.user.length > 0)) {
		// if there's a name in the query parameter, use it here
		filter = { "id" : req.body.user };
		let singleUser = await User.findOne(filter).catch(function (err) {
			console.log("Unable to find -", err);
		});
		if (singleUser) {
			data = {
				'Name' : singleUser.name,
				'Username' : singleUser.id,
				'Email' : singleUser.email,
				'Bio' : singleUser.bio
			}
		} else {
			searchResult = "Could not find user";
		}
	}
	res.render('userSearch', { searchResult, data });
});
app.get('/userSearch', (req, res) => {
	const searchResult = ` `;
	const data = {};
	res.render('userSearch', { searchResult, data });
});

app.post('/deletePost',  async function (req, res) {
	var deleteResult = "No post was specified.";
	if (req.body._id && (req.body._id.length > 0)) {
		var filter = { '_id' : req.body._id };
	
		let deleted = await Post.findOneAndDelete(filter).catch(function (err) {
			console.log("Unable to delete -", err);
			deleteResult = `There was an issue with deleting the post ${req.body._id}. Try again?`
		});
		if (deleted) {
			deleteResult = `Post ${req.body._id} was deleted.`
			 
		} else {
			deleteResult = `Post ${req.body._id} wasn't found in the database. Try again?`
		}
	}
	res.render('deletePost', { deleteResult });
}); 
app.get('/deletePost', (req, res) => {
	const deleteResult = ` `;
	res.render('deletePost', { deleteResult });
}); 


app.get('/createPost', (req, res) => {
	const postResult = ` `
	res.render('postForm', { postResult })
});
app.post('/createPost', async function(req, res) {
	if (req.body.user && req.body.price) {
		var postResult = ` `
		filter = {"id" : req.body.user};
		let usr = await User.findOne(filter).catch(function (err) {
			console.log("Issue encountered -", err);
		});
		if (!usr) {
			postResult = `The user that you are trying to make a post for does not exist.`;
			res.render('postForm', { postResult });
		} else {
			var newPost;
			try {
				// construct the Post from the form data which is in the request body
				newPost = new Post ({
					_id: mongoose.Types.ObjectId().toHexString(),
					user: req.body.user,
					price: req.body.price,
					desc: req.body.desc,
					status: true,
				});
				
			} catch (error) {
				postResult = `There was an issue creating this post. Check whether you filled out the fields correctly.`;
				res.render('postForm', { postResult });
			}


			let saved = await newPost.save().catch(function (err) {
				console.log("Issue encountered -", err);
			});
			if (saved) {
				postResult = `User ${req.body.user}\'s post was successfully added to the database!`;
			} else {
				postResult = `There was an issue saving this post. Try again?`;
			}
			res.render('postForm', { postResult });
		}
	} else {
		postResult = `You need to specify a user and a price in order to create post.`;
		res.render('postForm', { postResult })
	}
});


app.get('/createUser', (req, res) => {
	const userResult = ` `;
	res.render('userForm', { userResult });
});
app.post('/createUser', async function(req, res) {
	if (req.body.name && req.body.id && req.body.email) {
		var userResult = ` `
		filter = {"id" : req.body.id};
		let usr = await User.findOne(filter).catch(function (err) {
			console.log("Issue encountered -", err);
		});
		if (usr) {
			userResult = `Someone with that username already has an account with us. please try again.`;
			res.render('userForm', { userResult });
		} else {
			var newPost;
			try {
				// construct the User from the form data which is in the request body
				var newUser = new User ({
					name: req.body.name,
					id: req.body.id,
					email: req.body.email,
					bio: req.body.bio
					});
				
			} catch (error) {
				postResult = `There was an issue creating the user. Try again?`;
				res.render('userForm', { userResult });
			}

			let saved = await newUser.save().catch(function (err) {
				console.log("Issue encountered -", err);
			});
			if (saved) {
				userResult = `${req.body.name} has a new account with the username ${req.body.id}!`;
			} else {
				userResult = `There was an issue saving this user. Try again?`;
			}
			res.render('userForm', { userResult });
		}
	} else {
		postResult = `You need to specify your name, a username and an email in order to create a new user.`;
		res.render('userForm', { userResult })
	}
});

app.get('/postSearchByID', (req, res) => {
	const searchResult = ` `;
	const data = {};
	res.render('postSearchByID', { searchResult, data });
});
app.post('/postSearchByID', async function (req, res) {
	var searchResult = ` `;
	var data = {};
	var filter = {};
	if (req.body._id && (req.body._id.length > 0)) {
		// if there's a name in the query parameter, use it here
		filter = { "_id" : req.body._id };
		let singlePost = await Post.findOne(filter).catch(function (err) {
			console.log("Unable to find -", err);
		});
		if (singlePost) {
			data = {
				"ID" : singlePost._id,
				"User" : singlePost.user,
				"Price" : singlePost.price, 
				"Available" : singlePost.status,
				"Description" : singlePost.desc
			}
			 
		} else {
			searchResult = "Could not find a post with that ID.";
		}
	}
	res.render('postSearchByID', { searchResult, data });
});

app.get('/allPosts',  async function (req, res) {
	var data = [];
	let posts = await Post.find({}).catch(function (err) {
		console.log("Unable to find posts -", err);
	});
	if (posts && posts.length > 0) {
		for (var i = 0; i < posts.length; i++) {
			data.push({
				"ID" : posts[i]._id,
				"User" : posts[i].user,
				"Price" : posts[i].price, 
				"Available" : posts[i].status,
				"Description" : posts[i].desc
			})
		}
	}
	res.render('allPosts', { data });
});

app.get('/allUsers',  async function (req, res) {
	var data = [];
	let users = await User.find({}).catch(function (err) {
		console.log("Unable to find posts -", err);
	});
	if (users && users.length > 0) {
		for (var i = 0; i < users.length; i++) {
			data.push({
				"Name" : users[i].name,
				"Username" : users[i].id,
				"Email (@brynmawr.edu)" : users[i].email, 
				"Bio" : users[i].bio,

			})
		}
	}
	res.render('allUsers', { data });
});

app.get('/postSearchByUser', (req, res) => {
	const searchResult = ` `;
	const data = [];
	res.render('postSearchByUser', { searchResult, data });
});

app.post('/postSearchByUser',  async function (req, res) {
	var searchResult = ` `;
	var data = [];
	if (req.body.user && (req.body.user.length > 0)) {
		// if there's a name in the query parameter, use it here
		filter = { "id" : req.body.user };
		let singleUser = await User.findOne(filter).catch(function (err) {
			console.log("Unable to find -", err);
		});
		if (singleUser) {
			searchResult = `${singleUser.name}\'s Storefront:`
		} else {
			searchResult = `This user does not exist.`;
		}
		var filter = {"user" : req.body.user};
		let posts = await Post.find(filter).catch(function (err) {
			console.log("Unable to find posts -", err);
		});
		if (posts && posts.length > 0) {
			for (var i = 0; i < posts.length; i++) {
				data.push({
					"ID" : posts[i]._id,
					"User" : posts[i].user,
					"Price" : posts[i].price, 
					"Available" : posts[i].status,
					"Description" : posts[i].desc,
				})
			}
		}
	} else {
		searchResult = `Need to specify a user.`;
	}
	
	res.render('postSearchByUser', { searchResult, data });
});


app.post('/deleteUser',  async function (req, res) {
	var deleteResult = `No user was specified.`;
	var filter = {};
	if (req.body.user && (req.body.user.length > 0)) {
		filter = { 'id' : req.body.user };
		let deleted = await User.findOneAndDelete(filter).catch(function (err) {
			console.log("Unable to delete -", err);
			deleteResult = `There was an issue with deleting the user ${req.body.user}. Try again?`
			res.render('deleteUser', { deleteResult });
		});
		if (deleted) {
			filter = { 'user' : req.body.user };
			
			let posts = await Post.deleteMany(filter).catch(function (err) {
				console.log("Unable delete all posts from user -", err);
				deleteResult = `User ${req.body.user} was deleted, but there was an issue with deleting some of their posts.`
				res.render('deleteUser', { deleteResult });
			});
			deleteResult = `User ${req.body.user} and their posts were successfully deleted from the database.`

		} else {
			deleteResult = `User ${req.body.user} wasn't found in the database. Try again?`
		}
	}
	res.render('deleteUser', { deleteResult });
}); 
app.get('/deleteUser', (req, res) => {
	const deleteResult = ` `;
	res.render('deleteUser', { deleteResult });
}); 

app.post('/editPost',  async function (req, res) {
	var editResult = `No post was specified.`;
	if (req.body._id) {
		var filter = { '_id' : req.body._id };

		if (!(req.body.property == 'desc') && (!req.body.newValue || req.body.newValue.length == 0)) {
			editResult = `Cannot delete the contents of a required field.`;
		} else {
			const property = req.body.property;
			var newValue = req.body.newValue;
			
			var jsonObj = {};
			jsonObj[property] = newValue;
			if (isNaN(newValue) && property == 'price') {
				editResult = `Cannot set price to a non-numeric value. Try again?`
			} else {
				var action = { '$set' : jsonObj };
				editResult = `Post ${req.body._id} was successfully edited.`;
				let edited = await Post.findOneAndUpdate(filter, action).catch(function (err) {
					console.log("Unable to edit -", err);
					editResult = `There was an issue editing the post ${req.body._id}. Try again?`
				});
				if (!edited) {
					editResult = `The post ${req.body._id} was not found in the database. Try again?`
				}
			}		
		}		
	}
	res.render('editPost', { editResult });
}); 
app.get('/editPost', (req, res) => {
	const editResult = ` `;
	res.render('editPost', { editResult });
}); 

/*************************************************/
// just general setup
app.use(express.static(__dirname + '/views'));

app.listen(3000,  () => {
	console.log('Listening on port 3000');
    });


// set up Express
var express = require('express');
var app = express();

// set up BodyParser
var bodyParser = require('body-parser');
app.use(bodyParser.urlencoded({ extended: true }));

// import the Person class from Person.js
var Post = require('./Post.js');

/***************************************/

// endpoint for creating a new post
// this is the action of the "create new post" form
app.use('/create', (req, res) => {
	// construct the Post from the form data which is in the request body
	var newPost = new Post ({
		user: req.body.userID,
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

// endpoint for showing all the people
app.use('/all', (req, res) => {
	Post.find({}, (err, post) => {
		if (err) {
		    res.type('html').status(400);
		    console.log('uh oh' + err);
			res.json({'status': 'error'});
		    res.write(err);
		}
		else if (!post || post.length == 0){
			res.json({'status' : 'post not found'});
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
	});
});


// endpoint for showing a single user
app.use('/singleUser', (req, res) => {
	// construct the query object
	var filter = {};
	if (req.query.user) {
	    // if there's a name in the query parameter, use it here
	    filter = { "user" : req.query.user };
	} else {
		res.json({"status" : "No user was specified."});
		return;
	}

	Post.findOne(filter, (err, post) => {
		if (err) {
		    res.type('html').status(400);
		    console.log('uh oh' + err);
			res.json({'status': 'error'});
		    res.write(err);
		}
		else if (!post || post.length == 0) {
			res.json({'status' : 'User not found'});
		} else {
			res.type('html').status(400);
			user = post.user
			res.type('html').status(200);
			res.json( {
				'user': user
				// potentially add other information about the user.. maybe num posts?
			});
		}
	});
});





/*************************************************/

app.use('/public', express.static('public'));

app.use('/', (req, res) => { res.redirect('/public/postform.html'); } );

app.listen(3000,  () => {
	console.log('Listening on port 3000');
    });

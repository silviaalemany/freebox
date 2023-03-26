var mongoose = require('mongoose');

// the host:port must match the location where you are running MongoDB
// the "myDatabase" part can be anything you like
mongoose.connect('mongodb://127.0.0.1:27017/myDatabase');

var Schema = mongoose.Schema;

var userSchema = new Schema({
        // user's name
        name: {type: String, required: true, unique: true},
        // userID (kind of like an @, or username)
        id: String,
        // .edu email 
        email: String,
        // personal bio/info (could include pronouns, etc)
        bio: Boolean

    });

// export postSchema as a class called Post
module.exports = mongoose.model('Post', postSchema);
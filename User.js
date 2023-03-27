var mongoose = require('mongoose');

// the host:port must match the location where you are running MongoDB
// the "myDatabase" part can be anything you like
mongoose.connect('mongodb://127.0.0.1:27017/myDatabase');

var Schema = mongoose.Schema;

var userSchema = new Schema({
        // user's name
        name: {type: String, required: true},
        // userID (kind of like an @, or username)
        id: {type: String, required: true, unique: true},
        // .edu email 
        email: {type: String, required: true, unique: true},
        // personal bio/info (could include pronouns, etc)
        bio: {type: String}

    });

// export userSchema as a class called User
module.exports = mongoose.model('User', userSchema);
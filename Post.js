var mongoose = require('mongoose');

// the host:port must match the location where you are running MongoDB
// the "myDatabase" part can be anything you like
mongoose.connect('mongodb://127.0.0.1:27017/myDatabase');

var Schema = mongoose.Schema;

var postSchema = new Schema({
        user: {type: String, required: true, unique: true},
        // price (USD)
        price: Number,
        // desc is user description (supplemental)
        desc: String,
        // status: true if still available to purchase
        status: Boolean,
        //tags: strings to define object 
        tags: [String]

    });

// export postSchema as a class called Post
module.exports = mongoose.model('Post', postSchema);

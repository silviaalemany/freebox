var mongoose = require('mongoose');

// the host:port must match the location where you are running MongoDB
// the "myDatabase" part can be anything you like
mongoose.connect('mongodb://127.0.0.1:27017/myDatabase');

var Schema = mongoose.Schema;

var postSchema = new Schema({
        _id: String,
        user: {type: String, required: true},
        // price (USD)
        price: {type: Number, required: true},
        // desc is user description (supplemental)
        desc: {type: String},
        // status: true if still available to purchase
        status: {type: Boolean, required: true}
    });

// export postSchema as a class called Post
module.exports = mongoose.model('Post', postSchema);

var app = require('express')();
var http = require('http').Server(app);
var io = require('socket.io')(http);
var mongodb = require('mongodb');

var MongoClient = mongodb.MongoClient;
var url = 'mongodb://localhost:27017/login';

MongoClient.connect(url, function (err, db) {
    if (err) {
        console.log('Không kết nối được server mongoDB. Lỗi: ', err);
    } else {
        console.log('Đã kết nối tới', url);
        collection = db.collection('user');
        sanpham = db.collection('product');
    }
})

http.listen(3000, function () {
    console.log('Đang chạy ở cổng 3000');
})

// ket noi socket
io.on('connection', function (socket) {
    console.log('Kết nối thành công, socket.id: ', socket.id);

    // lang nghe su kien login
    socket.on('login', function (name, password) {
        console.log("LoginEvent: " + name + " và pass: " + password);
        var cursor = collection.find({ name: name });
        cursor.each(function (err, doc) {
            if (err) {
                console.log(err);
                socket.emit('login', false);
            } else {
                if (doc != null) {
                    if (doc.password == password) {
                        console.log("Đăng nhập thành công");
                        socket.emit('login', true);
                    } else {
                        console.log("Không đúng thông tin đăng nhập!");
                        socket.emit('login', false);
                    }
                }
            }
        });
    });


    // SIGN UP
    socket.on('register', function (name, email, password,phone) {
        console.log(user + " register");

        var user = { name: name, email: email, password: password,phone:phone };

        collection.insert(user, function (err, res) {
            if (err) {
                console.log(err);
                socket.emit('register', false);
            } else {
                console.log('New user inserted successful');
                socket.emit('register', true);
            }
        });
    });


    // GET PRODUCT
    socket.on('getProduct', function (msg) {
        console.log('Nhận lệnh getProduct từ Client: ' + msg);

        var cursor = sanpham.find();
        cursor.each(function (err, doc) {
            if (err) {
                console.log(err);
            } else if (doc != null) {
                var strings = JSON.parse(JSON.stringify(doc));
                console.log(strings);
                socket.emit('getProduct', strings);
            } else if (doc == null) {
                console.log('Kết thúc getProduct');
            }
        });
    });


    //ADD PRODUCT
    socket.on('addProduct', function (maSp, tenSp, nsx, giaBan, hinhSp) {
        console.log(product + " added");

        var product = { maSp: maSp, tenSp: tenSp, nsx: nsx, giaBan: giaBan, hinhSp: hinhSp };

        sanpham.insert(product, function (err, res) {
            if (err) {
                console.log(err);
                socket.emit('addProduct', false);
            } else {
                console.log('New product added successful');
                socket.emit('addProduct', true);
            }
        });
    });


    // DELETE PRODUCT
    socket.on('deleteProduct', function (id) {
        console.log(id + " deleted product");
        var o = { _id: new mongodb.ObjectID(id) };
        sanpham.remove(o, function (err, res) {
            if (err) {
                console.log(err);
                socket.emit('deleteProduct', false);
            } else {
                console.log('Product deleted successfully');
                socket.emit('deleteProduct', true);
            }
        });
    });


    // UPDATE PRODUCT
    socket.on('updateProduct', function (_id, maSp, tenSp, nsx, giaBan, hinhSp) {
        console.log(tenSp + " updated product");
        sanpham.update({ _id: new mongodb.ObjectID(_id) },
            {
                $set: { maSp: maSp, tenSp: tenSp, nsx: nsx, giaBan: giaBan, hinhSp: hinhSp }
            }, function (err, res) {
                if (err) {
                    console.log(err);
                    socket.emit('updateProduct', false);
                } else {
                    console.log('Updated successful');
                    socket.emit('updateProduct', true);
                }
            });
    });
});



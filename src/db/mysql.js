//引入mysql模块
var mysql = require('mysql')
//引入mysql配置数据
const {
    MYSQL_CONF
} = require('../config/db')
var conn
//连接数据库
function handleDisconnection() {
    //创建数据库
    var connection = mysql.createConnection(MYSQL_CONF);
    //连接数据库
    connection.connect(function (err) {
        if (err) {
            setTimeout(handleDisconnection(), 2000);
        }
    });
    //报错
    connection.on('error', function (err) {
        if (err.code === 'PROTOCOL_CONNECTION_LOST') {
            console.log('重连')
            handleDisconnection();
        } else {
            throw err;
        }
    });
    conn = connection
}
// 统一执行sql的函数
function exec(sql) {
    //连接数据库
    //每一次执行sql语句 重新连接数据库
    handleDisconnection()
    const promise = new Promise((resolve, reject) => {
        conn.query(sql, (err, result) => {
            if (err) return reject(err)
            return resolve(result)
        })
    })
    return promise
}
//暴露
module.exports = {
    exec
}

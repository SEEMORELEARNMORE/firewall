//引入处理post请求参数的组件
const bodyParser = require('body-parser');

//引入接口为/contact的路由
const contact = require('./src/router/contact')

const blacklist = require('./src/router/blacklist')

const call_logs = require("./src/router/call_logs")

const message_logs = require("./src/router/message_logs")

const permission = require("./src/router/permission")

//引入服务js
const app = require('./bin/www')
//防止跨域
app.all('*', function (req, res, next) {
    console.log('有请求')
    res.header("Access-Control-Allow-Origin", "*");
    res.header("Content-Type", "application/json;charset=utf-8");
    res.header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
    res.header('Access-Control-Allow-Methods', 'PUT, POST, GET, DELETE, OPTIONS');
    res.header('Access-Control-Allow-Headers', 'Content-Type, Content-Length, Authorization, Accept, X-Requested-With , yourHeaderFeild');
    if (req.method.toLowerCase() == 'options')
        res.send(200); //让options尝试请求快速结束
    else {
        next();
    }
});
//使用bodyParser对post请求参数进行处理
app.use(bodyParser.urlencoded({
    extended: false
}))
app.use(bodyParser.json())
//使用user路由
app.use('/firewall', contact)
app.use('/firewall', blacklist)
app.use('/firewall', call_logs)
app.use('/firewall',message_logs)
app.use("/firewall",permission)

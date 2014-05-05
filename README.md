soap-websocket-cxf
==================

SOAP over WebSocket based on CXF and SOAP over JMS

### Example flow:

![sample transaction flow](http://s29.postimg.org/xbqcwzpzb/cxf_websocket.png)

### Architecture:

Decoupling FE and BE allows asynchronous processing of requests by any of the BE instance. FE and BE could be decoupled and deployed on different machines in the data center. Usually applications will need much less FE than BE nodes / instances. This way, the following architecture is pretty useful:

![global architecture](http://s23.postimg.org/7p2vhkhor/soap_over_websocket_with_jms.png)

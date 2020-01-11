#!/usr/bin/env sh

nohup java -jar  ./ly-registry-1.0.0-SNAPSHOT.jar >eureka.out &
nohup java -jar  ./ly-user-service-1.0.0-SNAPSHOT.jar >user.out &
nohup java -jar  ./ly-sms-1.0.0-SNAPSHOT.jar >sms.out &
nohup java -jar  ./ly-auth-service-1.0.0-SNAPSHOT.jar >auth.out &
nohup java -jar  ./ly-gateway-1.0.0-SNAPSHOT.jar >zuul.out &
nohup java -jar  ./ly-item-service-1.0.0-SNAPSHOT.jar >item.out &
nohup java -jar  ./ly-upload-1.0.0-SNAPSHOT.jar >upload.out &
nohup java -jar  ./ly-page-1.0.0-SNAPSHOT.jar  >page.out &
nohup java -jar  ./ly-search-1.0.0-SNAPSHOT.jar >search.out &
nohup java -jar  ./ly-cart-1.0.0-SNAPSHOT.jar >cart.out &
nohup java -jar  ./ly-order-1.0.0-SNAPSHOT.jar >order.out &



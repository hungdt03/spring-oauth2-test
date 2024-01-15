package com.hungdt.socket.chatsocket.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.converter.DefaultContentTypeResolver;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.util.List;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        //  Dòng này kích hoạt một broker đơn giản (simple broker) và cho phép broadcast tin nhắn tới các subscriber đang đăng ký trên địa chỉ /user.
        //  Điều này có nghĩa là server có thể gửi tin nhắn đến các client thông qua broker với địa chỉ /user.
        registry.enableSimpleBroker("/topic", "/user");

        //  Định nghĩa tiền tố (prefix) cho các địa chỉ đích (destination) mà client có thể gửi tin nhắn đến server.
        //  Trong trường hợp này, client có thể gửi các tin nhắn đến server thông qua các địa chỉ bắt đầu bằng /app.
        registry.setApplicationDestinationPrefixes("/app");

        //  Xác định tiền tố cho các địa chỉ đích (destination) dành riêng cho từng người dùng.
        //  Điều này cho phép server gửi tin nhắn trực tiếp đến một người dùng cụ thể thông qua broker với địa chỉ dành riêng cho người dùng đó, bắt đầu bằng /user.
        registry.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .withSockJS();
    }


    @Override
    public boolean configureMessageConverters(List<MessageConverter> messageConverters) {

//        Phương thức này cấu hình các bộ chuyển đổi tin nhắn (message converters) để chuyển đổi các tin nhắn giữa các định dạng khác nhau.
//        Trong ví dụ này, một MappingJackson2MessageConverter được tạo ra để chuyển đổi các tin nhắn thành định dạng JSON bằng ObjectMapper của thư viện Jackson.
//        DefaultContentTypeResolver được sử dụng để xác định loại nội dung mặc định là JSON.

        DefaultContentTypeResolver resolver = new DefaultContentTypeResolver();
        resolver.setDefaultMimeType(MimeTypeUtils.APPLICATION_JSON);
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        converter.setObjectMapper(new ObjectMapper());
        converter.setContentTypeResolver(resolver);
        messageConverters.add(converter);
        return false;
    }
}

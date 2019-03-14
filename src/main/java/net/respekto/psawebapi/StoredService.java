package net.respekto.psawebapi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.function.Function;

@Service
public class StoredService {

    @Autowired
    private RedisTemplate<String, String> userTemplate;

    public void saveUser(String key, String user){
        userTemplate.opsForValue().set(key,user);
    }

    public String findById(final String userId) {
        final String user = userTemplate.opsForValue().get(userId);
        return user;
    }
}

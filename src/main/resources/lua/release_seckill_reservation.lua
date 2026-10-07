if redis.call('GET', KEYS[2]) ~= 'PENDING' then
    return 0
end

redis.call('DEL', KEYS[2])
redis.call('INCR', KEYS[1])
return 1

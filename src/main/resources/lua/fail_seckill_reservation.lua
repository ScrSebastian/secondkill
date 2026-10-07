if redis.call('GET', KEYS[2]) ~= 'PENDING' then
    return 0
end

redis.call('SET', KEYS[2], 'FAILED', 'EX', tonumber(ARGV[1]))
redis.call('INCR', KEYS[1])
return 1

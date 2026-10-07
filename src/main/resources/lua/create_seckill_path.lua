local count = redis.call('INCR', KEYS[1])
if count == 1 then
    redis.call('EXPIRE', KEYS[1], tonumber(ARGV[2]))
end
if count > tonumber(ARGV[1]) then
    return 0
end

redis.call('SET', KEYS[2], ARGV[3], 'EX', tonumber(ARGV[4]))
return 1

local expectedPath = redis.call('GET', KEYS[2])
if not expectedPath or expectedPath ~= ARGV[1] then
    return -1
end

redis.call('DEL', KEYS[2])

if redis.call('EXISTS', KEYS[3]) == 1 then
    return 2
end

local stock = tonumber(redis.call('GET', KEYS[1]))
if not stock or stock <= 0 then
    return 0
end

redis.call('DECR', KEYS[1])
redis.call('SET', KEYS[3], 'PENDING', 'EX', tonumber(ARGV[2]))
return 1

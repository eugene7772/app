local userKey = KEYS[1]
local searchKey = KEYS[2]
local searchJsonKey = KEYS[3]
local userJson = ARGV[1]

redis.call('SET', userKey, userJson)
redis.call('ZADD', searchKey, 0, userJson)
redis.call('DEL', searchJsonKey)

return 1

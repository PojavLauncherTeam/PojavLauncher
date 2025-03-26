//
// Created by BZLZHH on 2025/2/12.
//

#ifndef MOBILEGLUES_PLUGIN_CACHE_H
#define MOBILEGLUES_PLUGIN_CACHE_H

#include "../mg.h"
#include "../../config/config.h"
#include "../../config/settings.h"

#include <list>
#include <unordered_map>
#include <array>
#include <string>
#include <cstdint>

class Cache {
public:
    Cache();
    const char* get(const char* glsl);
    void put(const char* glsl, const char* essl);
    bool load();
    void save();

    static Cache& get_instance();
private:
    struct CacheEntry {
        std::array<uint8_t, 32> sha256;
        std::string essl;
        size_t size;
    };

    struct SHA256Hash {
        size_t operator()(const std::array<uint8_t, 32>& key) const;
    };

    std::list<CacheEntry> cacheList;
    using ListIterator = std::list<CacheEntry>::iterator;
    std::unordered_map<std::array<uint8_t, 32>, ListIterator, SHA256Hash> cacheMap;
    size_t cacheSize = 0;

    static std::array<uint8_t, 32> computeSHA256(const char* data);
    void maintainCacheSize();
};


#endif 

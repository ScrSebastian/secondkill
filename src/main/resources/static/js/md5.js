(function () {
    const shifts = [
        7, 12, 17, 22, 7, 12, 17, 22, 7, 12, 17, 22, 7, 12, 17, 22,
        5, 9, 14, 20, 5, 9, 14, 20, 5, 9, 14, 20, 5, 9, 14, 20,
        4, 11, 16, 23, 4, 11, 16, 23, 4, 11, 16, 23, 4, 11, 16, 23,
        6, 10, 15, 21, 6, 10, 15, 21, 6, 10, 15, 21, 6, 10, 15, 21
    ];
    const constants = [
        0xd76aa478, 0xe8c7b756, 0x242070db, 0xc1bdceee, 0xf57c0faf, 0x4787c62a, 0xa8304613, 0xfd469501,
        0x698098d8, 0x8b44f7af, 0xffff5bb1, 0x895cd7be, 0x6b901122, 0xfd987193, 0xa679438e, 0x49b40821,
        0xf61e2562, 0xc040b340, 0x265e5a51, 0xe9b6c7aa, 0xd62f105d, 0x02441453, 0xd8a1e681, 0xe7d3fbc8,
        0x21e1cde6, 0xc33707d6, 0xf4d50d87, 0x455a14ed, 0xa9e3e905, 0xfcefa3f8, 0x676f02d9, 0x8d2a4c8a,
        0xfffa3942, 0x8771f681, 0x6d9d6122, 0xfde5380c, 0xa4beea44, 0x4bdecfa9, 0xf6bb4b60, 0xbebfbc70,
        0x289b7ec6, 0xeaa127fa, 0xd4ef3085, 0x04881d05, 0xd9d4d039, 0xe6db99e5, 0x1fa27cf8, 0xc4ac5665,
        0xf4292244, 0x432aff97, 0xab9423a7, 0xfc93a039, 0x655b59c3, 0x8f0ccc92, 0xffeff47d, 0x85845dd1,
        0x6fa87e4f, 0xfe2ce6e0, 0xa3014314, 0x4e0811a1, 0xf7537e82, 0xbd3af235, 0x2ad7d2bb, 0xeb86d391
    ];

    function rotateLeft(value, amount) {
        return (value << amount) | (value >>> (32 - amount));
    }

    function toHexLittleEndian(value) {
        let result = "";
        for (let index = 0; index < 4; index++) {
            result += ((value >>> (index * 8)) & 0xff).toString(16).padStart(2, "0");
        }
        return result;
    }

    function md5(input) {
        const inputBytes = new TextEncoder().encode(input);
        const paddingLength = (56 - (inputBytes.length + 1) % 64 + 64) % 64;
        const message = new Uint8Array(inputBytes.length + 1 + paddingLength + 8);
        const bitLength = inputBytes.length * 8;
        message.set(inputBytes);
        message[inputBytes.length] = 0x80;

        for (let index = 0; index < 4; index++) {
            message[message.length - 8 + index] = (bitLength >>> (index * 8)) & 0xff;
        }

        let a0 = 0x67452301;
        let b0 = 0xefcdab89;
        let c0 = 0x98badcfe;
        let d0 = 0x10325476;

        for (let offset = 0; offset < message.length; offset += 64) {
            const words = new Uint32Array(16);
            for (let index = 0; index < 16; index++) {
                const wordOffset = offset + index * 4;
                words[index] = message[wordOffset] | (message[wordOffset + 1] << 8)
                    | (message[wordOffset + 2] << 16) | (message[wordOffset + 3] << 24);
            }

            let a = a0;
            let b = b0;
            let c = c0;
            let d = d0;

            for (let index = 0; index < 64; index++) {
                let f;
                let g;
                if (index < 16) {
                    f = (b & c) | (~b & d);
                    g = index;
                } else if (index < 32) {
                    f = (d & b) | (~d & c);
                    g = (5 * index + 1) % 16;
                } else if (index < 48) {
                    f = b ^ c ^ d;
                    g = (3 * index + 5) % 16;
                } else {
                    f = c ^ (b | ~d);
                    g = (7 * index) % 16;
                }

                const nextB = (b + rotateLeft((a + f + constants[index] + words[g]) >>> 0, shifts[index])) >>> 0;
                a = d;
                d = c;
                c = b;
                b = nextB;
            }

            a0 = (a0 + a) >>> 0;
            b0 = (b0 + b) >>> 0;
            c0 = (c0 + c) >>> 0;
            d0 = (d0 + d) >>> 0;
        }

        return toHexLittleEndian(a0) + toHexLittleEndian(b0) + toHexLittleEndian(c0) + toHexLittleEndian(d0);
    }

    globalThis.md5 = md5;
})();

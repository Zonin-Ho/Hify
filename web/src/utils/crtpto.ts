import CryptoJS from 'crypto-js';

export function encryptByAES(word: string, keyStr?: string) {
    return CryptoJS.AES.encrypt(word, keyStr = "123456").toString();
}

export function decryptByAES(word: string, keyStr?: string) {
    return CryptoJS.AES.decrypt(word, keyStr = "123456").toString(CryptoJS.enc.Utf8);
}

export function encryptByMD5(word: string) {
    // md5加盐加密
    return CryptoJS.MD5(word + 'hellohify').toString();
}
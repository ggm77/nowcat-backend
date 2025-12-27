package org.nowcat.nowcat.global.utils;

import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class ImageUtil {

    private static final byte[] JPEG = new byte[] {(byte)0xFF, (byte)0xD8};
    private static final byte[] PNG8 = new byte[] {(byte)0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A};

    /**
     * 파일의 헤더 16바이트만 읽어서 올바른 파일인지 확인하는 메서드
     * jpeg, jpg, png만 지원
     * @param header 파일 헤더 16바이트
     * @param extension 확장자
     * @return 올바른지 여부
     */
    public boolean isValidSignature(final byte[] header, final String extension){
        try{
            return switch (extension.toLowerCase(Locale.ROOT)) {
                case "jpg", "jpeg" -> isStrictJpeg(header);
                case "png" -> startsWith(header, PNG8);
                default -> false;
            };
        } catch (Exception ex) {
            return false;
        }
    }

    private boolean isStrictJpeg(final byte[] header) {
        // 1) FF D8로 시작하는가?
        if (header[0] != JPEG[0] || header[1] != JPEG[1]) {
            return false;
        }

        // 2) 세 번째 바이트가 FF인가? (마커 시작)
        if ((header[2] & 0xFF) != 0xFF) {
            return false;
        }

        // 3) 네 번째 바이트가 유효한 JPEG 마커인가?
        // E0~EF (APPn), DB (DQT), C0~CF (SOF)
        int marker = header[3] & 0xFF;
        return (marker >= 0xE0 && marker <= 0xEF) ||
                (marker == 0xDB) ||
                (marker >= 0xC0 && marker <= 0xCF);
    }

    //두개의 바이트 비교
    private static boolean startsWith(final byte[] data, final byte[] prefix){
        if(data.length < prefix.length){
            return false;
        }

        for(int i = 0; i < prefix.length; i++){
            if(prefix[i] != data[i]){
                return false;
            }
        }

        return true;
    }
}

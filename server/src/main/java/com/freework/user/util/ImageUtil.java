package com.freework.user.util;

import com.freework.common.loadon.util.PathUtil;
import com.freework.user.dto.ImageHolder;
import net.coobird.thumbnailator.Thumbnails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

/**
 * @author daihongru
 */
public class ImageUtil {
    private static Logger logger = LoggerFactory.getLogger(ImageUtil.class);

    /**
     * 将图片强制压缩成200px*200px，并存入指定的路径
     * path需包含文件名（含扩展名）
     * 异步执行
     *
     * @param path
     * @param imageHolder
     */
    public static void storageImage(String path, ImageHolder imageHolder) {
        File targetFile = new File(PathUtil.getBasePath() + path);
        try {
            Thumbnails.of(imageHolder.getImage()).size(200, 200).outputQuality(0.9f).keepAspectRatio(false).toFile(targetFile);
        } catch (IOException e) {
            logger.error(e.toString());
            throw new RuntimeException("storageImage：压缩并保存图片失败：" + e.toString());
        }
    }
}

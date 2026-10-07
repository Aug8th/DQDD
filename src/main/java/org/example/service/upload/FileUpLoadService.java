package org.example.service.upload;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import javax.imageio.ImageIO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@Service
public class FileUpLoadService {
    private final Path root;
    private final String baseUrl;

    public FileUpLoadService(@Value("${app.upload.dir:uploads}") String directory,
                             @Value("${app.base-url:http://localhost:8080}") String baseUrl) {
        this.root = Path.of(directory).toAbsolutePath().normalize();
        this.baseUrl = baseUrl.replaceAll("/+$", "");
    }

    public String saveImage(MultipartFile file, String type) {
        if (file == null || file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Image is empty");
        }
        if (file.getSize() > 10 * 1024 * 1024) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Image exceeds 10 MB");
        }
        if (!"avatar".equals(type) && !"recipe".equals(type)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid image type");
        }

        try (InputStream stream = file.getInputStream()) {
            var image = ImageIO.read(stream);
            if (image == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Only PNG, JPEG and GIF images are supported");
            }

            byte[] header = new byte[12];
            try (InputStream probe = file.getInputStream()) {
                probe.readNBytes(header, 0, header.length);
            }
            String extension = imageExtension(header);
            Path folder = root.resolve(type);
            Files.createDirectories(folder);
            String name = UUID.randomUUID() + "." + extension;

            try (InputStream content = file.getInputStream()) {
                Files.copy(content, folder.resolve(name));
            }
            return baseUrl + "/uploads/" + type + "/" + name;
        } catch (IOException ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Could not save image", ex);
        }
    }

    private String imageExtension(byte[] header) {
        if (header[0] == (byte) 0x89 && header[1] == 0x50
                && header[2] == 0x4e && header[3] == 0x47) {
            return "png";
        }
        if (header[0] == (byte) 0xff && header[1] == (byte) 0xd8
                && header[2] == (byte) 0xff) {
            return "jpg";
        }
        if (header[0] == 0x47 && header[1] == 0x49 && header[2] == 0x46) {
            return "gif";
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unsupported image format");
    }

    public Path root() {
        return root;
    }
}

package de.twomartens.wahlrecht.controller;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * used to show version and title information on html pages
 */
@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping(value = "/wahlrecht")
public class VersionHtmlController {

  @GetMapping(path = "/version")
  public String version() {
    return "version";
  }

  @ModelAttribute("version")
  private String getApplicationVersion() {
    return getTitle() + " " + getVersion();
  }

  @ModelAttribute("footerString")
  private String getApplicationVersion(@RequestHeader("host") String hostName) {
    return getTitle() + " " + getVersion() + " - " + hostName;
  }

  private String getTitle() {
    return Optional.ofNullable(VersionHtmlController.class.getPackage().getImplementationTitle())
        .filter(s -> !s.isBlank())
        .orElse("application");
  }

  public String getVersion() {
    return Optional.ofNullable(VersionHtmlController.class.getPackage().getImplementationVersion())
        .filter(s -> !s.isBlank())
        .orElse("DEVELOPER");
  }

  @ModelAttribute("hostname")
  private String getHostname() {
    try {
      return InetAddress.getLocalHost().getHostName();
    } catch (UnknownHostException e) {
      log.warn(e.toString(), e);
    }
    return "";
  }

  @ModelAttribute("manifest")
  private Collection<String> getManifest() {
    try {
      URL location = getClass().getProtectionDomain().getCodeSource().getLocation();
      String jarFileName = Paths.get(location.toURI()).toString();
      try (JarFile jarFile = new JarFile(jarFileName)) {
        ZipEntry entry = jarFile.getEntry(JarFile.MANIFEST_NAME);
        try (InputStream in = jarFile.getInputStream(entry)) {
          return new String(in.readAllBytes(), StandardCharsets.UTF_8).lines().toList();
        }
      }
    } catch (FileNotFoundException ignored) {
      // do nothing if manifest file is not available
    } catch (Exception e) {
      log.info(e.toString(), e);
    }
    return List.of(getTitle() + " " + getVersion());
  }


}

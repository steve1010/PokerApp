package entities.game.play;

import java.io.InputStream;

import javafx.scene.image.Image;

public class LocatedImage extends Image {

	private final String url;

	public LocatedImage(String url) {
		super(url);
		this.url = url;
	}

	public LocatedImage(InputStream resourceAsStream, String url) {
		super(resourceAsStream);
		this.url = url;
	}

	public String getUrl() {
		return url;
	}

}

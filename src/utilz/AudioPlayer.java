package utilz;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;
import java.net.URL;
import java.util.EnumMap;
import java.util.Map;

public class AudioPlayer {

    public enum Sound {
        AMBIENT("sounds/ambient.wav"),
        PLAYER_ATTACK("sounds/player_attack.wav"),
        PLAYER_ATTACK2("sounds/Player2Golpe.wav"),
        EGYPTIAN_MUSIC("sounds/EgyptianMusic.wav"),
        TUMBA_BOSS_MUSIC("sounds/TumbaFaraonMusic.wav"),
        GAME_OVER("sounds/GameOver.wav"),
        SALTO_PLAYER2("sounds/SaltoPlayer2.wav");

        private final String path;

        Sound(String path) {
            this.path = path;
        }
    }

    // Ajusta estos valores para subir/bajar volumen global
    public static float MUSIC_VOLUME = 0.4f;
    public static float SFX_VOLUME = 0.3f;

    private static final Map<Sound, Clip> clips = new EnumMap<>(Sound.class);
    private static final Map<Sound, Long> pausedPositions = new EnumMap<>(Sound.class);

    public static void playMusic(Sound sound, float volume) {
        Clip clip = getClip(sound);
        if (clip == null)
            return;

        if (clip.isRunning())
            clip.stop();

        clip.setFramePosition(0);
        setVolume(clip, volume);
        clip.loop(Clip.LOOP_CONTINUOUSLY);
        clip.start();
    }

    public static void stopMusic(Sound sound) {
        Clip clip = clips.get(sound);
        if (clip == null)
            return;
        clip.stop();
        clip.setFramePosition(0);
        pausedPositions.remove(sound);
    }

    public static void stopSound(Sound sound) {
        Clip clip = clips.get(sound);
        if (clip == null)
            return;
        clip.stop();
        clip.setFramePosition(0); // Reinicia el sonido al principio por si lo vuelves a reproducir
    }

    public static void playSfx(Sound sound, float volume) {
        Clip clip = getClip(sound);
        if (clip == null)
            return;

        if (clip.isRunning())
            clip.stop();

        clip.setFramePosition(0);
        setVolume(clip, volume);
        clip.start();
    }

    public static void pauseMusic(Sound sound) {
        Clip clip = clips.get(sound);
        if (clip == null)
            return;
        if (!clip.isRunning())
            return;

        pausedPositions.put(sound, clip.getMicrosecondPosition());
        clip.stop();
    }

    public static void resumeMusic(Sound sound, float volume) {
        Clip clip = getClip(sound);
        if (clip == null)
            return;

        setVolume(clip, volume);
        Long pos = pausedPositions.remove(sound);
        if (pos != null && !clip.isRunning())
            clip.setMicrosecondPosition(pos);

        if (!clip.isRunning()) {
            clip.loop(Clip.LOOP_CONTINUOUSLY);
            clip.start();
        }
    }

    private static Clip getClip(Sound sound) {
        if (clips.containsKey(sound))
            return clips.get(sound);

        Clip clip = loadClip(sound);
        clips.put(sound, clip);
        return clip;
    }

    private static Clip loadClip(Sound sound) {
        URL url = AudioPlayer.class.getResource("/res/" + sound.path);
        if (url == null) {
            System.err.println("Audio not found: /res/" + sound.path);
            return null;
        }

        try (AudioInputStream ais = AudioSystem.getAudioInputStream(url)) {
            Clip clip = AudioSystem.getClip();
            clip.open(ais);
            return clip;
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            System.err.println("Error loading audio: " + sound.path + " -> " + e.getMessage());
            return null;
        }
    }

    private static void setVolume(Clip clip, float volume) {
        if (!clip.isControlSupported(FloatControl.Type.MASTER_GAIN))
            return;

        float clamped = Math.max(0.0001f, Math.min(1.0f, volume));
        float db = 20.0f * (float) Math.log10(clamped);
        FloatControl gain = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
        gain.setValue(db);
    }
}

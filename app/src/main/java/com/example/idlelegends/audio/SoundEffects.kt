package com.example.idlelegends.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.math.PI
import kotlin.math.sin

object SoundManager {
    var soundEnabled: Boolean = true
    var musicEnabled: Boolean = true

    private const val SAMPLE_RATE = 22050
    private val audioScope = CoroutineScope(Dispatchers.Default)
    private var musicJob: Job? = null

    fun playAttack() {
        if (!soundEnabled) return
        audioScope.launch {
            // Whoosh slash sound
            val numSamples = (SAMPLE_RATE * 0.12).toInt()
            val buffer = ShortArray(numSamples)
            for (i in 0 until numSamples) {
                val progress = i.toFloat() / numSamples
                val freq = 450.0 - (progress * 300.0)
                val envelope = (1.0 - progress) * (1.0 - progress)
                val noise = (Math.random() * 2.0 - 1.0) * 0.4
                val tone = sin(2.0 * PI * i * freq / SAMPLE_RATE) * 0.6
                buffer[i] = ((tone + noise) * envelope * Short.MAX_VALUE * 0.45).toInt().toShort()
            }
            playPcm(buffer)
        }
    }

    fun playCrit() {
        if (!soundEnabled) return
        audioScope.launch {
            // Heavy crunch impact
            val numSamples = (SAMPLE_RATE * 0.2).toInt()
            val buffer = ShortArray(numSamples)
            for (i in 0 until numSamples) {
                val progress = i.toFloat() / numSamples
                val freq = 220.0 - (progress * 140.0)
                val envelope = Math.exp(-progress * 8.0)
                val tone = sin(2.0 * PI * i * freq / SAMPLE_RATE)
                val noise = (Math.random() * 2.0 - 1.0) * 0.5
                buffer[i] = ((tone * 0.7 + noise) * envelope * Short.MAX_VALUE * 0.8).toInt().toShort()
            }
            playPcm(buffer)
        }
    }

    fun playMagic() {
        if (!soundEnabled) return
        audioScope.launch {
            // Mystical rising shimmer
            val numSamples = (SAMPLE_RATE * 0.25).toInt()
            val buffer = ShortArray(numSamples)
            for (i in 0 until numSamples) {
                val progress = i.toFloat() / numSamples
                val freq = 520.0 + (progress * 500.0)
                val vibrato = sin(2.0 * PI * i * 18.0 / SAMPLE_RATE) * 50.0
                val envelope = sin(progress * PI)
                val tone = sin(2.0 * PI * i * (freq + vibrato) / SAMPLE_RATE)
                buffer[i] = (tone * envelope * Short.MAX_VALUE * 0.5).toInt().toShort()
            }
            playPcm(buffer)
        }
    }

    fun playCoin() {
        if (!soundEnabled) return
        audioScope.launch {
            // Two-tone bell ping
            val sampleCount1 = (SAMPLE_RATE * 0.08).toInt()
            val sampleCount2 = (SAMPLE_RATE * 0.12).toInt()
            val buffer = ShortArray(sampleCount1 + sampleCount2)
            for (i in 0 until sampleCount1) {
                val p = i.toFloat() / sampleCount1
                val tone = sin(2.0 * PI * i * 987.77 / SAMPLE_RATE) // B5
                buffer[i] = (tone * (1.0 - p) * Short.MAX_VALUE * 0.45).toInt().toShort()
            }
            for (i in 0 until sampleCount2) {
                val p = i.toFloat() / sampleCount2
                val tone = sin(2.0 * PI * i * 1318.51 / SAMPLE_RATE) // E6
                buffer[sampleCount1 + i] = (tone * (1.0 - p) * Short.MAX_VALUE * 0.5).toInt().toShort()
            }
            playPcm(buffer)
        }
    }

    fun playLevelUp() {
        if (!soundEnabled) return
        audioScope.launch {
            // Fanfare C5 - E5 - G5 - C6
            val notes = listOf(523.25, 659.25, 783.99, 1046.50)
            val noteDuration = (SAMPLE_RATE * 0.1).toInt()
            val buffer = ShortArray(noteDuration * notes.size)
            for (n in notes.indices) {
                val freq = notes[n]
                for (i in 0 until noteDuration) {
                    val p = i.toFloat() / noteDuration
                    val tone = sin(2.0 * PI * i * freq / SAMPLE_RATE)
                    buffer[n * noteDuration + i] = (tone * (1.0 - p * 0.5) * Short.MAX_VALUE * 0.5).toInt().toShort()
                }
            }
            playPcm(buffer)
        }
    }

    fun playBossRoar() {
        if (!soundEnabled) return
        audioScope.launch {
            val numSamples = (SAMPLE_RATE * 0.4).toInt()
            val buffer = ShortArray(numSamples)
            for (i in 0 until numSamples) {
                val p = i.toFloat() / numSamples
                val freq = 120.0 + sin(p * 20.0) * 30.0
                val rumble = (Math.random() * 2.0 - 1.0) * 0.4
                val tone = sin(2.0 * PI * i * freq / SAMPLE_RATE) * 0.7
                buffer[i] = ((tone + rumble) * (1.0 - p * 0.6) * Short.MAX_VALUE * 0.6).toInt().toShort()
            }
            playPcm(buffer)
        }
    }

    fun playHit() {
        if (!soundEnabled) return
        audioScope.launch {
            val numSamples = (SAMPLE_RATE * 0.08).toInt()
            val buffer = ShortArray(numSamples)
            for (i in 0 until numSamples) {
                val p = i.toFloat() / numSamples
                val freq = 150.0 - p * 70.0
                val tone = sin(2.0 * PI * i * freq / SAMPLE_RATE)
                buffer[i] = (tone * (1.0 - p) * Short.MAX_VALUE * 0.4).toInt().toShort()
            }
            playPcm(buffer)
        }
    }

    fun startBgm() {
        if (musicJob?.isActive == true) return
        musicJob = audioScope.launch {
            // Fantasy harp melody sequence
            val melody = listOf(
                392.00, 440.00, 523.25, 587.33,
                659.25, 587.33, 523.25, 440.00,
                392.00, 523.25, 659.25, 783.99,
                659.25, 587.33, 523.25, 392.00
            )
            while (isActive) {
                if (musicEnabled) {
                    for (freq in melody) {
                        if (!isActive || !musicEnabled) break
                        playTone(freq, 280)
                        delay(320)
                    }
                } else {
                    delay(500)
                }
            }
        }
    }

    fun stopBgm() {
        musicJob?.cancel()
        musicJob = null
    }

    private fun playTone(freq: Double, durationMs: Int) {
        val numSamples = (SAMPLE_RATE * (durationMs / 1000.0)).toInt()
        val buffer = ShortArray(numSamples)
        for (i in 0 until numSamples) {
            val p = i.toFloat() / numSamples
            val envelope = sin(p * PI) * (1.0 - p * 0.5)
            val tone = sin(2.0 * PI * i * freq / SAMPLE_RATE)
            buffer[i] = (tone * envelope * Short.MAX_VALUE * 0.12).toInt().toShort()
        }
        playPcm(buffer)
    }

    private fun playPcm(buffer: ShortArray) {
        try {
            val audioTrack = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_GAME)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(SAMPLE_RATE)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(buffer.size * 2)
                .setTransferMode(AudioTrack.MODE_STATIC)
                .build()

            audioTrack.write(buffer, 0, buffer.size)
            audioTrack.play()
            // Release after playback completed
            audioScope.launch {
                val durationMs = (buffer.size * 1000L) / SAMPLE_RATE + 50L
                delay(durationMs)
                try {
                    audioTrack.stop()
                    audioTrack.release()
                } catch (_: Exception) {}
            }
        } catch (_: Exception) {
            // AudioTrack creation might fail on constrained devices; silently ignore
        }
    }
}

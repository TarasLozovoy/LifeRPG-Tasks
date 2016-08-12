package com.levor.liferpgtasks.controller;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

import com.levor.liferpgtasks.R;

public class AudioController {

    private int levelUpSoundID;
    private int skillsUpSoundID;
    private SoundPool soundPool;
    private Context context;

    private static AudioController AudioController;
    public static AudioController getInstance(Context context){
        if (AudioController == null){
            AudioController = new AudioController(context);
        }
        return AudioController;
    }

    AudioController(Context context) {
        this.context = context;

        //init SoundPool
        soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);

        // soundId for Later handling of sound pool
        levelUpSoundID = soundPool.load(context, R.raw.level_up, 1);
        skillsUpSoundID = soundPool.load(context, R.raw.skill_up, 1);
    }

    public void playLevelUpSound() {
        if (!LifeController.getInstance(context).getSharedPreferences().getBoolean(LifeController.DISABLE_SOUNDS_TAG, false)) {
            soundPool.play(levelUpSoundID, 1, 1, 0, 0, 1);
        }
    }

    public void playSkillUpSound() {
        if (!LifeController.getInstance(context).getSharedPreferences().getBoolean(LifeController.DISABLE_SOUNDS_TAG, false)) {
            soundPool.play(skillsUpSoundID, 1, 1, 0, 0, 1);
        }
    }
}

package com.levor.liferpgtasks.dataBase;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.levor.liferpgtasks.dataBase.DataBaseSchema.SkillsTable;
import com.levor.liferpgtasks.model.Characteristic;
import com.levor.liferpgtasks.model.LifeEntity;
import com.levor.liferpgtasks.model.Skill;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.UUID;

public class SkillsCursorWrapper extends CursorWrapper {
    private LifeEntity lifeEntity;

    public SkillsCursorWrapper(Cursor cursor, LifeEntity lifeEntity) {
        super(cursor);
        this.lifeEntity = lifeEntity;
    }

    public Skill getSkill() {
        String uuid = getString(getColumnIndex(SkillsTable.Cols.UUID));
        String title = getString(getColumnIndex(SkillsTable.Cols.TITLE));
        String keyCharString = getString(getColumnIndex(SkillsTable.Cols.KEY_CHARACTERISTC_TITLE));
        int level = getInt(getColumnIndex(SkillsTable.Cols.LEVEL));
        double sublevel = getFloat(getColumnIndex(SkillsTable.Cols.SUBLEVEL));

        //parsing related characteristics and impact
        TreeMap<Characteristic, Integer> charsImpactMap = new TreeMap<>();
        String[] charsArray = keyCharString.split(Skill.CHAR_CHAR_DB_DIVIDER);
        for (String s : charsArray) {
            if (s.equals("")) continue;
            if (!s.contains(Skill.CHAR_IMPACT_DB_DIVIDER)) {
                s += Skill.CHAR_IMPACT_DB_DIVIDER + "100";
            }
            String charTitle = s.split(Skill.CHAR_IMPACT_DB_DIVIDER)[0];
            int impact = Integer.parseInt(s.split(Skill.CHAR_IMPACT_DB_DIVIDER)[1]);
            Characteristic characteristic = lifeEntity.getCharacteristicByTitle(charTitle);
            if (characteristic == null) {
                characteristic = lifeEntity.getCharacteristicById(UUID.fromString(charTitle));
            }
            if (characteristic != null) {
                charsImpactMap.put(characteristic, impact);
            }
        }
        return new Skill(title, level, sublevel, UUID.fromString(uuid), charsImpactMap);
    }
}

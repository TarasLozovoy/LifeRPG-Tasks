package com.levor.liferpg.dataBase;

public class DataBaseSchema {
    public static final class HeroTable {
        public static final String NAME = "real_life_hero";

        public static final class Cols {
            public static final String NAME = "hero_name";
            public static final String LEVEL = "hero_level";
            public static final String XP = "hero_xp";
            public static final String BASEXP = "hero_basexp";
        }
    }

    public static final class CharacteristicsTable {
        public static final String NAME = "real_life_characteristics";

        public static final class Cols {
            public static final String TITLE = "characteristic_title";
            public static final String LEVEL = "characteristic_level";
        }
    }
    public static final class SkillsTable {
        public static final String NAME = "real_life_skills";

        public static final class Cols {
            public static final String TITLE = "skill_title";
            public static final String LEVEL = "skill_level";
            public static final String SUBLEVEL = "skill_sublevel";
            public static final String UUID = "skill_uuid";
            public static final String KEY_CHARACTERISTC_TITLE = "skill_key_characteristic_title";
        }
    }

    public static final class TasksTable {
        public static final String NAME = "real_life_tasks";

        public static final class Cols {
            public static final String TITLE = "task_title";
            public static final String UUID = "task_uuid";
            public static final String RELATED_SKILLS = "task_related_skills";
            public static final String REPEATABILITY = "task_repeatability";
            public static final String DIFFICULTY = "task_difficulty";
            public static final String IMPORTANCE = "task_importance";
            public static final String DATE = "task_date";
        }
    }
}

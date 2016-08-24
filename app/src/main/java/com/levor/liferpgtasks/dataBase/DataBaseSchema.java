package com.levor.liferpgtasks.dataBase;

public class DataBaseSchema {
    public static final class HeroTable {
        public static final String NAME = "real_life_hero";

        public static final class Cols {
            public static final String NAME = "hero_name";
            public static final String LEVEL = "hero_level";
            public static final String XP = "hero_xp";
            public static final String BASEXP = "hero_basexp";
            public static final String MONEY = "hero_money";
        }
    }

    public static final class CharacteristicsTable {
        public static final String NAME = "real_life_characteristics";

        public static final class Cols {
            public static final String TITLE = "characteristic_title";
            public static final String LEVEL = "characteristic_level";
            public static final String ID = "characteristic_id";
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
            public static final String FINISH_DATE = "task_finish_date";
            public static final String NOTIFY = "task_notify";
            public static final String DATE_MODE = "date_mode";
            public static final String REPEAT_MODE = "repeat_mode";
            public static final String REPEAT_DAYS_OF_WEEK = "repeat_days_of_week";
            public static final String REPEAT_INDEX = "repeat_index";
            public static final String HABIT_DAYS = "habit_days";
            public static final String HABIT_DAYS_LEFT = "habit_days_left";
            public static final String HABIT_START_DATE = "habit_start_date";
            public static final String NUMBER_OF_EXECUTIONS = "number_of_executions";
        }
    }

    public static final class MiscTable {
        public static final String NAME = "real_life_misc";

        public static final class Cols {
            public static final String ACHIEVES_LEVELS = "achievements_levels";
            public static final String STATISTICS_NUMBERS = "statistics_numbers";
            public static final String IMAGE_AVATAR = "hero_image_avatar";
            public static final String IMAGE_AVATAR_MODE = "hero_image_avatar_mode";
        }
    }

    public static final class TasksPerDayTable {
        public static final String NAME = "real_life_tasks_per_day";

        public static final class Cols {
            public static final String DATE = "date";
            public static final String TASKS_PERFORMED = "tasks_performed";
        }
    }

    public static final class RewardsTable {
        public static final String NAME = "real_life_rewards";

        public static final class Cols {
            public static final String TITLE = "reward_title";
            public static final String COST = "reward_cost";
            public static final String ID = "reward_id";
            public static final String DESCRIPTION = "reward_description";
            public static final String DONE = "reward_done";
        }
    }
}

package com.nju.apcd.constant;

public class Constants {
    public static final String DATA_DIR = "D:/IdeaProjects/apcd/data";
    public static final String BIGQUERY_DATA_DIR = DATA_DIR + "/bigquery_data";
    public static final String EVENT_LOG_DIR = DATA_DIR + "/event_log";
    public static final String LOG_ALL_SCENE_DIR = DATA_DIR + "/log_all_scene";
    public static final String LOG_SINGLE_SCENE_DIR = DATA_DIR + "/log_single_scene";
    public static final String[] SCENE_LIST = {"fork_merge", "fork_close", "unfork_merge", "unfork_close"};
    public static final String COMMITTER = "committer";
    public static final String MAINTAINER = "maintainer";
    public static final String REVIEWER = "reviewer";
    public static final String[] ALIGNMENTS_HEADER = {"people","pr_number","scene","fitness"};
    public static final String[] TBR_HEADER = {"people","pr_number","scene","trace_fitness","missing_tokens","consumed_tokens","remaining_tokens","produced_tokens"};
}

package com.pda.uhf_g.util;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

/* loaded from: classes.dex */
public class AppStateTracker {
    public static final int STATE_BACKGROUND = 1;
    public static final int STATE_FOREGROUND = 0;
    private static int currentState;

    public interface AppStateChangeListener {
        void appTurnIntoBackGround();

        void appTurnIntoForeground();
    }

    public static int getCurrentState() {
        return currentState;
    }

    public static void track(Application application, final AppStateChangeListener appStateChangeListener) {
        application.registerActivityLifecycleCallbacks(new SimpleActivityLifecycleCallbacks() { // from class: com.pda.uhf_g.util.AppStateTracker.1
            private int resumeActivityCount;

            {
                super();
                this.resumeActivityCount = 0;
            }

            @Override // com.pda.uhf_g.util.AppStateTracker.SimpleActivityLifecycleCallbacks, android.app.Application.ActivityLifecycleCallbacks
            public void onActivityStarted(Activity activity) {
                if (this.resumeActivityCount == 0) {
                    int unused = AppStateTracker.currentState = 0;
                    AppStateChangeListener.this.appTurnIntoForeground();
                }
                this.resumeActivityCount++;
            }

            @Override // com.pda.uhf_g.util.AppStateTracker.SimpleActivityLifecycleCallbacks, android.app.Application.ActivityLifecycleCallbacks
            public void onActivityStopped(Activity activity) {
                int i = this.resumeActivityCount - 1;
                this.resumeActivityCount = i;
                if (i == 0) {
                    int unused = AppStateTracker.currentState = 1;
                    AppStateChangeListener.this.appTurnIntoBackGround();
                }
            }
        });
    }

    private static class SimpleActivityLifecycleCallbacks implements Application.ActivityLifecycleCallbacks {
        private SimpleActivityLifecycleCallbacks() {
        }

        @Override // android.app.Application.ActivityLifecycleCallbacks
        public void onActivityCreated(Activity activity, Bundle bundle) {
        }

        @Override // android.app.Application.ActivityLifecycleCallbacks
        public void onActivityStarted(Activity activity) {
        }

        @Override // android.app.Application.ActivityLifecycleCallbacks
        public void onActivityResumed(Activity activity) {
        }

        @Override // android.app.Application.ActivityLifecycleCallbacks
        public void onActivityPaused(Activity activity) {
        }

        @Override // android.app.Application.ActivityLifecycleCallbacks
        public void onActivityStopped(Activity activity) {
        }

        @Override // android.app.Application.ActivityLifecycleCallbacks
        public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {
        }

        @Override // android.app.Application.ActivityLifecycleCallbacks
        public void onActivityDestroyed(Activity activity) {
        }
    }
}

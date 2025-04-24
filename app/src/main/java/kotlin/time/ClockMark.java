package kotlin.time;

import androidx.constraintlayout.core.motion.utils.TypedValues;
import kotlin.Metadata;

/* compiled from: Clock.kt */
@Metadata(bv = {1, 0, 3}, d1 = {"\u0000\u001c\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\b\b'\u0018\u00002\u00020\u0001B\u0005¢\u0006\u0002\u0010\u0002J\u0010\u0010\u0003\u001a\u00020\u0004H&ø\u0001\u0000¢\u0006\u0002\u0010\u0005J\u0006\u0010\u0006\u001a\u00020\u0007J\u0006\u0010\b\u001a\u00020\u0007J\u001b\u0010\t\u001a\u00020\u00002\u0006\u0010\n\u001a\u00020\u0004H\u0096\u0002ø\u0001\u0000¢\u0006\u0004\b\u000b\u0010\fJ\u001b\u0010\r\u001a\u00020\u00002\u0006\u0010\n\u001a\u00020\u0004H\u0096\u0002ø\u0001\u0000¢\u0006\u0004\b\u000e\u0010\f\u0082\u0002\u0004\n\u0002\b\u0019¨\u0006\u000f"}, d2 = {"Lkotlin/time/ClockMark;", "", "()V", "elapsedNow", "Lkotlin/time/Duration;", "()D", "hasNotPassedNow", "", "hasPassedNow", "minus", TypedValues.TransitionType.S_DURATION, "minus-LRDsOJo", "(D)Lkotlin/time/ClockMark;", "plus", "plus-LRDsOJo", "kotlin-stdlib"}, k = 1, mv = {1, 1, 15})
/* loaded from: classes.dex */
public abstract class ClockMark {
    public abstract double elapsedNow();

    /* renamed from: plus-LRDsOJo */
    public ClockMark mo934plusLRDsOJo(double duration) {
        return new AdjustedClockMark(this, duration, null);
    }

    /* renamed from: minus-LRDsOJo, reason: not valid java name */
    public ClockMark m935minusLRDsOJo(double duration) {
        return mo934plusLRDsOJo(Duration.m979unaryMinusimpl(duration));
    }

    public final boolean hasPassedNow() {
        return !Duration.m959isNegativeimpl(elapsedNow());
    }

    public final boolean hasNotPassedNow() {
        return Duration.m959isNegativeimpl(elapsedNow());
    }
}

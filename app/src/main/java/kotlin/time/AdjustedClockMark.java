package kotlin.time;

import androidx.constraintlayout.core.motion.utils.TypedValues;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;

/* compiled from: Clock.kt */
@Metadata(bv = {1, 0, 3}, d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\f\b\u0003\u0018\u00002\u00020\u0001B\u0018\u0012\u0006\u0010\u0002\u001a\u00020\u0001\u0012\u0006\u0010\u0003\u001a\u00020\u0004ø\u0001\u0000¢\u0006\u0002\u0010\u0005J\u0010\u0010\u000b\u001a\u00020\u0004H\u0016ø\u0001\u0000¢\u0006\u0002\u0010\u0007J\u001b\u0010\f\u001a\u00020\u00012\u0006\u0010\r\u001a\u00020\u0004H\u0096\u0002ø\u0001\u0000¢\u0006\u0004\b\u000e\u0010\u000fR\u0016\u0010\u0003\u001a\u00020\u0004ø\u0001\u0000¢\u0006\n\n\u0002\u0010\b\u001a\u0004\b\u0006\u0010\u0007R\u0011\u0010\u0002\u001a\u00020\u0001¢\u0006\b\n\u0000\u001a\u0004\b\t\u0010\n\u0082\u0002\u0004\n\u0002\b\u0019¨\u0006\u0010"}, d2 = {"Lkotlin/time/AdjustedClockMark;", "Lkotlin/time/ClockMark;", "mark", "adjustment", "Lkotlin/time/Duration;", "(Lkotlin/time/ClockMark;DLkotlin/jvm/internal/DefaultConstructorMarker;)V", "getAdjustment", "()D", "D", "getMark", "()Lkotlin/time/ClockMark;", "elapsedNow", "plus", TypedValues.TransitionType.S_DURATION, "plus-LRDsOJo", "(D)Lkotlin/time/ClockMark;", "kotlin-stdlib"}, k = 1, mv = {1, 1, 15})
/* loaded from: classes.dex */
final class AdjustedClockMark extends ClockMark {
    private final double adjustment;
    private final ClockMark mark;

    private AdjustedClockMark(ClockMark mark, double adjustment) {
        this.mark = mark;
        this.adjustment = adjustment;
    }

    public /* synthetic */ AdjustedClockMark(ClockMark mark, double adjustment, DefaultConstructorMarker $constructor_marker) {
        this(mark, adjustment);
    }

    public final double getAdjustment() {
        return this.adjustment;
    }

    public final ClockMark getMark() {
        return this.mark;
    }

    @Override // kotlin.time.ClockMark
    public double elapsedNow() {
        return Duration.m961minusLRDsOJo(this.mark.elapsedNow(), this.adjustment);
    }

    @Override // kotlin.time.ClockMark
    /* renamed from: plus-LRDsOJo */
    public ClockMark mo934plusLRDsOJo(double duration) {
        return new AdjustedClockMark(this.mark, Duration.m962plusLRDsOJo(this.adjustment, duration), null);
    }
}

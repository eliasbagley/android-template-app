package com.rocketmade.templateapp.util;

import android.support.test.runner.AndroidJUnit4;
import android.test.AndroidTestCase;

import com.rocketmade.templateapp.utils.Paginator;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.ResponseBody;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;


import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;

import static org.mockito.Mockito.*;
import static com.google.common.truth.Truth.*;

/**
 * Created by eliasbagley on 2/2/16.
 */

@RunWith(AndroidJUnit4.class)
public class PaginatorTest extends AndroidTestCase {
    private static final int PAGE_SIZE = 10;

    Call               successCall;
    Call               failureCall;
    Callback           callback;
    Paginator.Pageable pageable;
    Paginator          paginator;

    @Before
    public void init() {
        successCall = mock(Call.class);
        failureCall = mock(Call.class);
        callback = mock(Callback.class);

        doAnswer(invocation -> {
            Callback callback = invocation.getArgumentAt(0, Callback.class);
            callback.onResponse(Response.success(""), null /* not used */);
            return null;
        }).when(successCall).enqueue(any(Callback.class));

        doAnswer(invocation -> {
            Callback callback = invocation.getArgumentAt(0, Callback.class);
            ResponseBody responseBody = ResponseBody.create(MediaType.parse("application/json"), "");
            callback.onResponse(Response.error(404, responseBody), null /* not used */);
            return null;
        }).when(failureCall).enqueue(any(Callback.class));

        pageable = mock(Paginator.Pageable.class);
        when(pageable.pageableRequestForPage(anyInt())).thenReturn(new Paginator.PageableRequest(successCall, callback));

        paginator = new Paginator(PAGE_SIZE, pageable);
    }

    @Test
    public void testThatCallGetsEnqueued() {
        verify(successCall).enqueue(any(Callback.class));
    }

    @Test
    public void testThatItRequestsAPageableRequest() {
        verify(pageable).pageableRequestForPage(1);
    }

    @Test
    public void verifyLoadingCallbackStarts() {
        verify(pageable).isLoading(true);
    }

    @Test
    public void verifyCallbackIsCalled() {
        verify(callback).onResponse(any(), any());
    }

    @Test
    public void verifyLoadingCallbackFinishesAndUpdatesCurrentPage() {
        assertThat(paginator.currentPage()).isEqualTo(2);
    }

    @Test
    public void verifyPageDoesNotUpdateOnFailure() {
        pageable = mock(Paginator.Pageable.class);
        when(pageable.pageableRequestForPage(anyInt())).thenReturn(new Paginator.PageableRequest(failureCall, callback));
        paginator = new Paginator(PAGE_SIZE, pageable);

        assertThat(paginator.currentPage()).isEqualTo(1);
    }

    @Test
    public void testSuccessfulResponse() {
        // Test the ordering of the isLoading callback
        InOrder order = inOrder(pageable);
        order.verify(pageable).isLoading(true);
        order.verify(pageable).isLoading(false);
    }

    @Test
    public void testScrollDown() {
        paginator.onScroll(null, 6, 10, 10); // Scroll down to trigger a page load
        verify(pageable).pageableRequestForPage(2);
    }

    @Test
    public void testScrollDownFurther() {
        paginator.onScroll(null, 6, 10 /* unused */, 10 /* unused */); // Scroll down to trigger a page load
        verify(pageable).pageableRequestForPage(2);

        paginator.onScroll(null, 15, 10 /* unused */, 10 /* unused */); // Scroll down to trigger a page load
        verify(pageable).pageableRequestForPage(3);
    }

    @Test
    public void testThatRefreshClearsState() {
        InOrder order = inOrder(pageable);

        order.verify(pageable).pageableRequestForPage(1);

        paginator.refresh();

        order.verify(pageable).pageableRequestForPage(1);

    }


    @Test
    public void assertThatNoPagesLoadedForBlankPaginator() {
        pageable = mock(Paginator.Pageable.class);
        when(pageable.pageableRequestForPage(anyInt())).thenReturn(new Paginator.PageableRequest(failureCall, callback));
        paginator = new Paginator(PAGE_SIZE, pageable);

        assertThat(paginator.alreadyLoadedPage(1)).isFalse();
    }

    @Test
    public void testThatPageWasLoaded() {
        assertThat(paginator.alreadyLoadedPage(1)).isTrue();
    }


    @Ignore //TODO something about calling cancel() on a background thread is messing with this test, I think
    @Test
    public void testCancelling() {
        paginator.cleanup();
        verify(successCall).cancel();
    }

}

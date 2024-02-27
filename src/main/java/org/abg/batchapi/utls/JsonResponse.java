package org.abg.batchapi.utls;

import jakarta.annotation.Nullable;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JsonResponse<T> {
    private boolean status;
    private String message;
    @Nullable
    private T data;
    private long pageCount;
    private long totalCount;
}

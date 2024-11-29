package com.example.webgrow.payload.request;

import com.example.webgrow.payload.dto.TimelineEntryDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BulkTimelineEntryRequest {
    private List<TimelineEntryDto> entries;
}

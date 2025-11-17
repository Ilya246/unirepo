package ru.ssau.tk._AMEBA_._PESEZ_.dto.request;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import org.jetbrains.annotations.NotNull;
import ru.ssau.tk._AMEBA_._PESEZ_.utility.Utility;



@SuperBuilder
//@AllArgsConstructor
//@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
//@JsonIgnoreProperties(ignoreUnknown = true)

public class PointRequest {
     @JsonProperty("functionId")
     private Long functionId;

     @JsonProperty("xValue")
     private Double xValue;

     @JsonProperty("yValue")
     private Double yValue;

     // Конструктор по умолчанию (ВАЖНО для Jackson)
     public PointRequest() {
          Utility.Log.info("🔧 PointRequest no-args constructor called");
     }

     // Конструктор со всеми параметрами
     public PointRequest(Long functionId, Double xValue, Double yValue) {
          Utility.Log.info("🔧 PointRequest all-args constructor called: {}, {}, {}", functionId, xValue, yValue);
          this.functionId = functionId;
          this.xValue = xValue;
          this.yValue = yValue;
     }

     // СЕТТЕРЫ (самое важное!)
     public void setFunctionId(Long functionId) {
          Utility.Log.info("🔄 PointRequest.setFunctionId() called with: {}", functionId);
          this.functionId = functionId;
     }

     public void setXValue(Double xValue) {
          Utility.Log.info("🔄 PointRequest.setXValue() called with: {}", xValue);
          this.xValue = xValue;
     }

     public void setYValue(Double yValue) {
          Utility.Log.info("🔄 PointRequest.setYValue() called with: {}", yValue);
          this.yValue = yValue;
     }

     // Геттеры
     public Long getFunctionId() {
          Utility.Log.info("📤 PointRequest.getFunctionId() called - returning: {}", functionId);
          return functionId;
     }

     public Double getXValue() {
          Utility.Log.info("📤 PointRequest.getXValue() called - returning: {}", xValue);
          return xValue;
     }

     public Double getYValue() {
          Utility.Log.info("📤 PointRequest.getYValue() called - returning: {}", yValue);
          return yValue;
     }

     @Override
     public String toString() {
          return "PointRequest{" +
                  "functionId=" + functionId +
                  ", xValue=" + xValue +
                  ", yValue=" + yValue +
                  '}';
     }
}

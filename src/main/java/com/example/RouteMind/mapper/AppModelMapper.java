package com.example.RouteMind.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.NullValueCheckStrategy;
import static com.example.RouteMind.constants.MapperConstants.*;
import com.example.RouteMind.dto.Request.CreateOrderRequest;
import com.example.RouteMind.entity.Order;

@Mapper(componentModel = MapperConstants.SPRING, nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public abstract class AppModelMapper {

    @Mappings({
        @Mapping(source = PICKUP_ADDRESS_NAME, target = PICKUP_NAME),
        @Mapping(source = PICKUP_ADDRESS_PHONE, target = PICKUP_PHONE),
        @Mapping(source = PICKUP_ADDRESS_ADDRESS, target = PICKUP_ADDRESS),
        @Mapping(source = PICKUP_ADDRESS_CITY, target = PICKUP_CITY),
        @Mapping(source = PICKUP_ADDRESS_STATE, target = PICKUP_STATE),
        @Mapping(source = PICKUP_ADDRESS_PINCODE, target = PICKUP_PINCODE),
        @Mapping(source = DELIVERY_ADDRESS_NAME, target = DELIVERY_NAME),
        @Mapping(source = DELIVERY_ADDRESS_PHONE, target = DELIVERY_PHONE),
        @Mapping(source = DELIVERY_ADDRESS_ADDRESS, target = DELIVERY_ADDRESS),
        @Mapping(source = DELIVERY_ADDRESS_CITY, target = DELIVERY_CITY),
        @Mapping(source = DELIVERY_ADDRESS_STATE, target = DELIVERY_STATE),
        @Mapping(source = DELIVERY_ADDRESS_PINCODE, target = DELIVERY_PINCODE),
        @Mapping(source = ORDER_VALUE, target = COD_VALUE),
        @Mapping(target = ID, ignore = true),
        @Mapping(target = CREATED_AT, ignore = true),
        @Mapping(target = UPDATED_AT, ignore = true),
        @Mapping(target = STATUS, ignore = true),
        @Mapping(target = ORDER_DIMENSIONS, ignore = true),
        @Mapping(target = PRODUCT_DESCRIPTION, ignore = true),
        @Mapping(target = PICKUP_EMAIL, ignore = true),
        @Mapping(target = DELIVERY_EMAIL, ignore = true)
    })
    public abstract Order createOrderRequestToOrder(CreateOrderRequest request);

}

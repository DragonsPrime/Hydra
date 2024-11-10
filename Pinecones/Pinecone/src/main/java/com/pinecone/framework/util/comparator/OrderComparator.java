package com.pinecone.framework.util.comparator;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import com.pinecone.framework.system.Nullable;
import com.pinecone.framework.util.ObjectUtils;

public class OrderComparator implements Comparator<Object> {
    public static final OrderComparator INSTANCE = new OrderComparator();

    public OrderComparator() {
    }

    public Comparator<Object> withSourceProvider(OrderComparator.OrderSourceProvider sourceProvider) {
        return (o1, o2) -> {
            return this.doCompare(o1, o2, sourceProvider);
        };
    }

    @Override
    public int compare(@Nullable Object o1, @Nullable Object o2) {
        return this.doCompare(o1, o2, (OrderComparator.OrderSourceProvider)null);
    }

    private int doCompare(@Nullable Object o1, @Nullable Object o2, @Nullable OrderComparator.OrderSourceProvider sourceProvider) {
        boolean p1 = o1 instanceof PriorityOrdered;
        boolean p2 = o2 instanceof PriorityOrdered;
        if (p1 && !p2) {
            return -1;
        }
        else if (p2 && !p1) {
            return 1;
        }
        else {
            int i1 = this.getOrder(o1, sourceProvider);
            int i2 = this.getOrder(o2, sourceProvider);
            return Integer.compare(i1, i2);
        }
    }

    private int getOrder(@Nullable Object obj, @Nullable OrderComparator.OrderSourceProvider sourceProvider) {
        Integer order = null;
        if (obj != null && sourceProvider != null) {
            Object orderSource = sourceProvider.getOrderSource(obj);
            if (orderSource != null) {
                if (orderSource.getClass().isArray()) {
                    Object[] ta = ObjectUtils.toObjectArray(orderSource);
                    int len = ta.length;

                    for( int i = 0; i < len; ++i ) {
                        Object source = ta[i];
                        order = this.findOrder(source);
                        if (order != null) {
                            break;
                        }
                    }
                }
                else {
                    order = this.findOrder(orderSource);
                }
            }
        }

        return order != null ? order : this.getOrder(obj);
    }

    protected int getOrder( @Nullable Object obj ) {
        if (obj != null) {
            Integer order = this.findOrder(obj);
            if (order != null) {
                return order;
            }
        }

        return 2147483647;
    }

    @Nullable
    protected Integer findOrder(Object obj) {
        return obj instanceof Ordered ? ((Ordered)obj).getOrder() : null;
    }

    @Nullable
    public Integer getPriority(Object obj) {
        return null;
    }

    public static void sort(List<?> list) {
        if (list.size() > 1) {
            list.sort(INSTANCE);
        }
    }

    public static void sort(Object[] array) {
        if (array.length > 1) {
            Arrays.sort(array, INSTANCE);
        }

    }

    public static void sortIfNecessary(Object value) {
        if ( value instanceof Object[] ) {
            OrderComparator.sort((Object[])((Object[])value));
        }
        else if (value instanceof List) {
            OrderComparator.sort((List)value);
        }

    }

    @FunctionalInterface
    public interface OrderSourceProvider {
        @Nullable
        Object getOrderSource(Object var1);
    }
}

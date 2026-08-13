package common.cn.kafei.simukraft.compat.ldlib.utils.search;

@FunctionalInterface
public interface IResultHandler<T> {
    void acceptResult(T result);
}

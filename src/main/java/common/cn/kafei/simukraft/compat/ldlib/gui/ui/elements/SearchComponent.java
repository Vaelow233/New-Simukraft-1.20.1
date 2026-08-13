package common.cn.kafei.simukraft.compat.ldlib.gui.ui.elements;

import com.lowdragmc.lowdraglib.gui.widget.SearchComponentWidget;
import com.lowdragmc.lowdraglib.utils.Position;
import com.lowdragmc.lowdraglib.utils.Size;
import common.cn.kafei.simukraft.compat.ldlib.gui.ui.UIElement;
import common.cn.kafei.simukraft.compat.ldlib.gui.ui.utils.UIElementProvider;
import common.cn.kafei.simukraft.compat.ldlib.utils.search.IResultHandler;

import java.util.function.Consumer;

public class SearchComponent<T> extends UIElement {
    private final SearchComponentWidget<T> delegate;
    private final SearchStyle searchStyle = new SearchStyle();
    private UIElementProvider<T> candidateProvider;
    public final TextField textField = new TextField();

    public SearchComponent(ISearchUI<T> searchUI) {
        delegate = new SearchComponentWidget<>(0, 0, 1, 1, new SearchComponentWidget.IWidgetSearch<>() {
            @Override
            public void search(String word, Consumer<T> resultConsumer) {
                searchUI.search(word, resultConsumer::accept);
            }

            @Override
            public String resultDisplay(T result) {
                return searchUI.resultText(result);
            }

            @Override
            public void selectResult(T result) {
                searchUI.onResultSelected(result);
            }
        });
        addWidget(delegate);
    }

    public SearchComponent<T> setCandidateUIProvider(UIElementProvider<T> provider) {
        candidateProvider = provider;
        return this;
    }

    public SearchComponent<T> searchStyle(Consumer<SearchStyle> consumer) {
        consumer.accept(searchStyle);
        delegate.setCapacity(searchStyle.maxItemCount);
        return this;
    }

    @Override
    protected void afterLayout() {
        delegate.setSelfPosition(new Position(0, 0));
        delegate.setSize(new Size(getSizeWidth(), getSizeHeight()));
    }

    public interface ISearchUI<T> {
        void search(String word, IResultHandler<T> handler);
        String resultText(T result);
        void onResultSelected(T result);
    }

    public final class SearchStyle {
        private int maxItemCount = 8;
        private boolean closeAfterSelect = true;

        public SearchStyle maxItemCount(int value) { maxItemCount = Math.max(1, value); return this; }
        public SearchStyle closeAfterSelect(boolean value) { closeAfterSelect = value; return this; }
    }
}

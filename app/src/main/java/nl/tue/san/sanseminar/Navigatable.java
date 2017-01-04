package nl.tue.san.sanseminar;

import android.content.Context;
import android.view.View;

/**
 * Created by Maurice on 4-1-2017.
 *
 */

public interface Navigatable {

    /**
     * Gets the properties for navigation purposes.
     */
    Properties getProperties();

    /**
     * Class representing the properties that are required for Fragments to be used in MainActivity
     */
    class Properties {

        private final int fabIconResource;
        private final View.OnClickListener fabClickListener;
        private final Object title;

        private static final int NONE = -1;

        public boolean usesFloatingActionButton(){
            return this.fabIconResource != NONE && this.fabClickListener != null;
        }

        public int getFabIconResource(){
            return this.fabIconResource;
        }

        public View.OnClickListener getFabClickListener(){
            return this.fabClickListener;
        }

        public boolean usesTitle(){
            return this.title != null;
        }



        private Properties(int fabIconResource, View.OnClickListener fabClickListener, Object title) {
            this.fabIconResource = fabIconResource;
            this.fabClickListener = fabClickListener;
            this.title = title;
        }

        public String getTitle(Context context) {
            if(title instanceof String)
                return (String) title;
            else
                return context.getString((int)title);
        }


        public static class Builder{

            private int fabIconResource = Properties.NONE;
            private View.OnClickListener fabClickListener = null;

            private Object title = null;


            /**
             * Use the given resource as the icon on the FloatingActionButton
             * @param fabIconResource
             * @return This builder for chaining.
             */
            public Builder useFabIcon(int fabIconResource){
                this.fabIconResource = fabIconResource;
                return this;
            }

            /**
             * The OnClickListener to use to handle click events on the Floating Action Button
             * @param listener The listener to use to handle click events on the Floating Action Button.
             * @return This builder for chaining.
             */
            public Builder useFabHandler(View.OnClickListener listener){
                this.fabClickListener = listener;
                return this;
            }

            /**
             * Use the given text as title.
             * @param title The text to use as a title.
             * @return This builder for chaining.
             */
            public Builder useTitle(String title){
                this.title = title;
                return this;
            }

            /**
             * Use the given resource as title.
             * @param title The resource to use as a title.
             * @return This builder for chaining.
             */
            public Builder useTitle(int title){
                this.title = title;
                return this;
            }



            /**
             * Create a non-mutable Properties object using the properties set on this builder.
             * The builder does not become invalid after calling this method, and as such it is
             * possible to use it to create multiple Properties objects.
             * @return The created properties.
             */
            public Properties build(){
                return new Properties(this.fabIconResource, this.fabClickListener, this.title, this.menuResource);
            }

        }
    }

}

/*******************************************************************************
 * Copyright (c) 2010 Actuate Corporation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *  Actuate Corporation  - initial API and implementation
 *******************************************************************************/

package org.eclipse.birt.chart.reportitem.ui;

import org.eclipse.birt.chart.reportitem.ui.dialogs.ChartExpressionProvider;
import org.eclipse.birt.chart.ui.swt.interfaces.IUIServiceProvider;
import org.eclipse.birt.chart.util.ChartUtil;
import org.eclipse.birt.report.designer.util.DEUtil;
import org.eclipse.birt.report.model.api.DesignElementHandle;
import org.eclipse.birt.report.model.api.ExtendedItemHandle;
import org.eclipse.birt.report.model.api.StyleHandle;
import org.eclipse.birt.report.model.api.elements.DesignChoiceConstants;
import org.eclipse.birt.report.model.api.extension.ExtendedElementException;
import org.eclipse.birt.report.model.api.extension.IReportItem;
import org.eclipse.birt.report.model.api.metadata.DimensionValue;

/**
 * ChartReportItemUIUtil
 * 
 * @since 2.5.3
 */

public class ChartReportItemUIUtil
{

	/**
	 * Creates chart filter factory instance according to specified item handle.
	 * 
	 * @param item
	 * @return filter factory
	 * @throws ExtendedElementException
	 */
	public static ChartFilterFactory createChartFilterFactory( Object item )
			throws ExtendedElementException
	{
		if ( item instanceof ExtendedItemHandle )
		{
			return getChartFilterFactory( ( (ExtendedItemHandle) item ).getReportItem( ) );
		}
		else if ( item instanceof IReportItem )
		{
			return createChartFilterFactory( item );
		}
		return new ChartFilterFactory( );
	}

	private static ChartFilterFactory getChartFilterFactory(
			IReportItem adaptableObj )
	{
		ChartFilterFactory factory = ChartUtil.getAdapter( adaptableObj,
				ChartFilterFactory.class );
		if ( factory != null )
		{
			return factory;
		}

		return new ChartFilterFactory( );
	}

	/**
	 * Returns the categories list in BIRT chart expression builder
	 * 
	 * @param builderCommand
	 * @return category style
	 */
	public static int getExpressionBuilderStyle( int builderCommand )
	{
		if ( builderCommand == IUIServiceProvider.COMMAND_EXPRESSION_DATA_BINDINGS )
		{
			return ChartExpressionProvider.CATEGORY_WITH_BIRT_VARIABLES
					| ChartExpressionProvider.CATEGORY_WITH_COLUMN_BINDINGS
					| ChartExpressionProvider.CATEGORY_WITH_REPORT_PARAMS;
		}
		else if ( builderCommand == IUIServiceProvider.COMMAND_EXPRESSION_CHART_DATAPOINTS )
		{
			return ChartExpressionProvider.CATEGORY_WITH_DATA_POINTS;
		}
		else if ( builderCommand == IUIServiceProvider.COMMAND_EXPRESSION_SCRIPT_DATAPOINTS )
		{
			// Script doesn't support column binding expression.
			return ChartExpressionProvider.CATEGORY_WITH_DATA_POINTS
					| ChartExpressionProvider.CATEGORY_WITH_REPORT_PARAMS
					| ChartExpressionProvider.CATEGORY_WITH_JAVASCRIPT;
		}
		else if ( builderCommand == IUIServiceProvider.COMMAND_EXPRESSION_TRIGGERS_SIMPLE )
		{
			// Bugzilla#202386: Tooltips never support chart
			// variables. Use COMMAND_EXPRESSION_TRIGGERS_SIMPLE for un-dp
			return ChartExpressionProvider.CATEGORY_WITH_REPORT_PARAMS
					| ChartExpressionProvider.CATEGORY_WITH_JAVASCRIPT;
		}
		else if ( builderCommand == IUIServiceProvider.COMMAND_EXPRESSION_TOOLTIPS_DATAPOINTS )
		{
			// Bugzilla#202386: Tooltips never support chart
			// variables. Use COMMAND_EXPRESSION_TOOLTIPS_DATAPOINTS for dp
			return ChartExpressionProvider.CATEGORY_WITH_REPORT_PARAMS
					| ChartExpressionProvider.CATEGORY_WITH_COLUMN_BINDINGS
					| ChartExpressionProvider.CATEGORY_WITH_DATA_POINTS;
		}
		else if ( builderCommand == IUIServiceProvider.COMMAND_CUBE_EXPRESSION_TOOLTIPS_DATAPOINTS )
		{
			return ChartExpressionProvider.CATEGORY_WITH_REPORT_PARAMS
					| ChartExpressionProvider.CATEGORY_WITH_DATA_POINTS;
		}
		else if ( builderCommand == IUIServiceProvider.COMMAND_HYPERLINK )
		{
			return ChartExpressionProvider.CATEGORY_WITH_BIRT_VARIABLES
					| ChartExpressionProvider.CATEGORY_WITH_REPORT_PARAMS;
		}
		else if ( builderCommand == IUIServiceProvider.COMMAND_HYPERLINK_DATAPOINTS )
		{
			return ChartExpressionProvider.CATEGORY_WITH_BIRT_VARIABLES
					| ChartExpressionProvider.CATEGORY_WITH_COLUMN_BINDINGS
					| ChartExpressionProvider.CATEGORY_WITH_REPORT_PARAMS
					| ChartExpressionProvider.CATEGORY_WITH_DATA_POINTS;
		}
		else if ( builderCommand == IUIServiceProvider.COMMAND_HYPERLINK_DATAPOINTS_SIMPLE )
		{
			// Used for data cube case, no column bindings allowed
			return ChartExpressionProvider.CATEGORY_WITH_BIRT_VARIABLES
					| ChartExpressionProvider.CATEGORY_WITH_REPORT_PARAMS
					| ChartExpressionProvider.CATEGORY_WITH_DATA_POINTS;
		}
		else if ( builderCommand == IUIServiceProvider.COMMAND_HYPERLINK_LEGEND )
		{
			// Add Legend item variables and remove column bindings
			return ChartExpressionProvider.CATEGORY_WITH_LEGEND_ITEMS
					| ChartExpressionProvider.CATEGORY_WITH_REPORT_PARAMS
					| ChartExpressionProvider.CATEGORY_WITH_JAVASCRIPT
					| ChartExpressionProvider.CATEGORY_WITH_BIRT_VARIABLES;
		}
		return ChartExpressionProvider.CATEGORY_BASE;
	}
	
	
	/**
	 * Get background image setting from design element handle.
	 * 
	 * @param handle
	 *            The handle of design element.
	 * @return background image
	 */
	public static String getBackgroundImage( DesignElementHandle handle )
	{
		return handle.getStringProperty( StyleHandle.BACKGROUND_IMAGE_PROP );
	}

	/**
	 * Get background position settings from design element handle.
	 * 
	 * @param handle
	 *            The handle of design element.
	 * @return background position
	 */
	public static Object[] getBackgroundPosition( DesignElementHandle handle )
	{
		Object x = null;
		Object y = null;

		if ( handle != null )
		{
			Object px = handle.getProperty( StyleHandle.BACKGROUND_POSITION_X_PROP );
			Object py = handle.getProperty( StyleHandle.BACKGROUND_POSITION_Y_PROP );

			if ( px instanceof String )
			{
				x = px;
			}
			else if ( px instanceof DimensionValue )
			{
				// {0%,0%}
				if ( DesignChoiceConstants.UNITS_PERCENTAGE.equals( ( (DimensionValue) px ).getUnits( ) ) )
				{
					x = px;
				}
				else
				{
					// {1cm,1cm}
					x = Integer.valueOf( (int) DEUtil.convertoToPixel( px ) );
				}
			}

			if ( py instanceof String )
			{
				y = py;
			}
			else if ( py instanceof DimensionValue )
			{
				// {0%,0%}
				if ( DesignChoiceConstants.UNITS_PERCENTAGE.equals( ( (DimensionValue) py ).getUnits( ) ) )
				{
					y = py;
				}
				else
				{
					// {1cm,1cm}
					y = Integer.valueOf( (int) DEUtil.convertoToPixel( py ) );
				}
			}
		}
		return new Object[]{
				x, y
		};
	}

	/**
	 * Get background repeat property from design element handle.
	 * 
	 * @param handle
	 *            The handle of design element.
	 * @return background repeat property
	 */
	public static int getBackgroundRepeat( DesignElementHandle handle )
	{
		return getRepeat( handle.getStringProperty( StyleHandle.BACKGROUND_REPEAT_PROP ) );
	}
	
	/**
	 *  Get repeat identifier according to its value
	 *  
	 * @param repeat
	 * 	Given string
	 * @return
	 * 	The repeat value
	 */
	public static int getRepeat( String repeat )
	{
		if ( DesignChoiceConstants.BACKGROUND_REPEAT_REPEAT_X.equals( repeat ) )
		{
			return 1;
		}
		else if ( DesignChoiceConstants.BACKGROUND_REPEAT_REPEAT_Y.equals( repeat ) )
		{
			return 2;
		}
		else if ( DesignChoiceConstants.BACKGROUND_REPEAT_REPEAT.equals( repeat ) )
		{
			return 3;
		}
		return 0;
	}
}

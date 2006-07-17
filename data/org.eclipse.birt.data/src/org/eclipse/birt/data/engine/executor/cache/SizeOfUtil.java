/*******************************************************************************
 * Copyright (c) 2004, 2005 Actuate Corporation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http)){}//www.eclipse.org/legal/epl-v10.html
 *
 * Contributors)){}
 *  Actuate Corporation  - initial API and implementation
 *******************************************************************************/
package org.eclipse.birt.data.engine.executor.cache;

import java.math.BigDecimal;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;

import org.eclipse.birt.data.engine.core.DataException;
import org.eclipse.birt.data.engine.odi.IResultClass;
import org.eclipse.birt.data.engine.odi.IResultObject;

/**
 * This class provide the function of compute the size of memory occupied by
 * object.
 */
class SizeOfUtil
{
	private final static int INTEGER_SIZE = 16;
	private final static int DOUBLE_SIZE = 16;	
	private final static int DATE_SIZE = 24;
	private final static int TIME_SIZE = 24;
	
	private static int BIGDECIMAL_SIZE = 80;
	private static int TIMESTAMP_SIZE = 24;

	// field count of result object
	private int fieldCount = 0;
	private boolean[] isfixedSize = null;
	private int[] fieldSize = null;

	static
	{
		if ( System.getProperty( "java.version" ).startsWith( "1.5" ) )
		{
			BIGDECIMAL_SIZE = 96;
			TIMESTAMP_SIZE = 32;
		}
	}

	/**
	 * 
	 * @param resultClass
	 * @throws DataException
	 */
	SizeOfUtil( IResultClass resultClass ) throws DataException
	{
		fieldCount = resultClass.getFieldCount( );
		isfixedSize = new boolean[resultClass.getFieldCount( )];
		fieldSize = new int[resultClass.getFieldCount( )];

		for ( int i = 1; i <= resultClass.getFieldCount( ); i++ )
		{
			if ( isFixedSizeClass( resultClass.getFieldValueClass( i ) ) )
			{
				fieldSize[i - 1] = sizeOf( resultClass.getFieldValueClass( i ) );
				isfixedSize[i - 1] = true;
			}
			else
			{
				isfixedSize[i - 1] = false;
			}
		}
	}

	/**
	 * Return whether a class is fixed size.
	 * 
	 * @param objectClass
	 * @return
	 */
	private static boolean isFixedSizeClass( Class objectClass )
	{
		return objectClass.equals( Integer.class )
				|| objectClass.equals( Double.class )
				|| objectClass.equals( BigDecimal.class )
				|| objectClass.equals( Date.class )
				|| objectClass.equals( Time.class )
				|| objectClass.equals( Timestamp.class );
	}

	/**
	 * Return the size of memory occupied by fixed size class object.
	 * 
	 * @param objectClass
	 * @return
	 */
	private static int sizeOf( Class objectClass )
	{
		if ( objectClass.equals( Integer.class ) )
		{
			return SizeOfUtil.INTEGER_SIZE;
		}
		else if ( objectClass.equals( Double.class ) )
		{
			return SizeOfUtil.DOUBLE_SIZE;
		}
		else if ( objectClass.equals( BigDecimal.class ) )
		{
			return SizeOfUtil.BIGDECIMAL_SIZE;
		}
		else if ( objectClass.equals( Date.class ) )
		{
			return SizeOfUtil.DATE_SIZE;
		}
		else if ( objectClass.equals( Time.class ) )
		{
			return SizeOfUtil.TIME_SIZE;
		}
		else if ( objectClass.equals( Timestamp.class ) )
		{
			return SizeOfUtil.TIMESTAMP_SIZE;
		}
		else
		{
			// Normally followed lines will never be arrived.
			assert ( false );
			return 0;
		}
	}

	/**
	 * Compute the size of memory occupied by result object
	 * 
	 * @param resultObject
	 * @return
	 * @throws DataException
	 */
	int sizeOf( IResultObject resultObject ) throws DataException
	{
		int returnValue = 0;
		for ( int i = 1; i <= fieldCount; i++ )
		{
			if ( !isfixedSize[i - 1] )
			{
				returnValue += sizeOf( resultObject.getResultClass( )
						.getFieldValueClass( i ),
						resultObject.getFieldValue( i ) );
			}
			else
			{
				if ( resultObject.getFieldValue( i ) != null )
				{
					returnValue += fieldSize[i - 1];
				}
			}
		}
		int fieldsSize = 16 + ( 4 + fieldCount * 4 - 1 ) / 8 * 8;
		returnValue += 16 + ( 4 + fieldsSize - 1 ) / 8 * 8;
		return returnValue;
	}

	/**
	 * Return the size of memory occupied by variable size class object.
	 * 
	 * @param objectClass
	 * @param object
	 * @return
	 */
	private static int sizeOf( Class objectClass, Object object )
	{
		if ( object == null )
		{
			return 0;
		}
		else if ( objectClass.equals( String.class ) )
		{
			int strLen = ( (String) object ).length( );
			return 40 + ( ( strLen + 1 ) / 4 ) * 8;
		}
		else if ( objectClass.equals( byte[].class ) )
		{
			int byteLen = ( (byte[]) object ).length;
			return 16 + ( 4 + byteLen - 1 ) / 8 * 8;
		}
		else
		{
			// Normally followed lines will never be arrived.
			assert ( false );
			return 0;
		}
	}

}

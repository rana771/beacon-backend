package com.athena.mis.application.taglib

import com.athena.mis.PluginConnector
import com.athena.mis.integration.accounting.AccPluginConnector
import com.athena.mis.integration.arms.ArmsPluginConnector
import com.athena.mis.integration.budget.BudgPluginConnector
import com.athena.mis.integration.datapipeline.DataPipeLinePluginConnector
import com.athena.mis.integration.document.DocumentPluginConnector
import com.athena.mis.integration.elearning.ELearningPluginConnector
import com.athena.mis.integration.exchangehouse.ExchangeHousePluginConnector
import com.athena.mis.integration.fixedasset.FxdPluginConnector
import com.athena.mis.integration.inventory.InvPluginConnector
import com.athena.mis.integration.procurement.ProcPluginConnector
import com.athena.mis.integration.projecttrack.PtPluginConnector
import com.athena.mis.integration.qsmeasurement.QsPluginConnector
import com.athena.mis.integration.sarb.SarbPluginConnector
import grails.plugin.springsecurity.SpringSecurityService

/**
 * PluginTagLib methods check if plugin installed for given name(s)
 */

class PluginTagLib {

    static namespace = "app"

    SpringSecurityService springSecurityService

    private static final String STR_NAME = 'name'
    private static final String STR_NAMES = 'names'
    private static final String COMMA = ','
    private static final String EMPTY_SPACE = ''
    private static final String SPACE_CHARACTER = "\\s"

    /**
     * Renders the body if the specified plugin is installed
     * attr takes the attribute 'name' as plugin name
     * example: <app:ifPlugin name="Accounting"></app:ifPlugin>
     *
     * @attr name REQUIRED -the plugin name
     */

    def ifPlugin = { attrs, body ->
        String strPluginName = attrs.remove(STR_NAME)
        strPluginName = strPluginName.replaceAll(SPACE_CHARACTER, EMPTY_SPACE)
        if (hasPlugin(strPluginName)) {
            out << body()
        } else {
            out << EMPTY_SPACE
        }
    }

    /**
     * Renders the body if all specified plugins are installed
     * attr takes the attribute 'names' as plugin name
     * example: <app:ifAllPlugins names="Accounting,Inventory"></app:ifAllPlugins>
     *
     * @attr names REQUIRED -comma separated plugin names
     */
    def ifAllPlugins = { attrs, body ->
        String strPluginNames = attrs.remove(STR_NAMES)
        strPluginNames = strPluginNames.replaceAll(SPACE_CHARACTER, EMPTY_SPACE)

        if (strPluginNames.size() <= 0) {
            out << EMPTY_SPACE
            return
        }
        boolean allInstalled = true
        List<String> lstPlugins = strPluginNames.split(COMMA);
        for (int i = 0; i < lstPlugins.size(); i++) {
            String pluginName = lstPlugins[i]
            if (pluginName.length() == 0) {
                out << EMPTY_SPACE
                return
            }
            if (!hasPlugin(pluginName)) {
                allInstalled = false
                break
            }
        }
        if (allInstalled) {
            out << body()
        } else {
            out << EMPTY_SPACE
        }
    }

   /**
    * Check if given plugin is installed using PluginConnector class
    * @param strPluginName -name of the plugin
    * @return - true(if plugin installed), false(otherwise)
    */
    private boolean hasPlugin(String strPluginName) {
        switch (strPluginName) {
            case AccPluginConnector.PLUGIN_NAME:
                if (PluginConnector.isPluginInstalled(AccPluginConnector.PLUGIN_NAME)) {
                    return true
                }
                break
            case PluginConnector.PLUGIN_NAME:
                if (PluginConnector.isPluginInstalled(PluginConnector.PLUGIN_NAME)) {
                    return true
                }
                break
            case BudgPluginConnector.PLUGIN_NAME:
                if (PluginConnector.isPluginInstalled(BudgPluginConnector.PLUGIN_NAME)) {
                    return true
                }
                break
            case InvPluginConnector.PLUGIN_NAME:
                if (PluginConnector.isPluginInstalled(InvPluginConnector.PLUGIN_NAME)) {
                    return true
                }
                break
            case ProcPluginConnector.PLUGIN_NAME:
                if (PluginConnector.isPluginInstalled(ProcPluginConnector.PLUGIN_NAME)) {
                    return true
                }
                break
            case QsPluginConnector.PLUGIN_NAME:
                if (PluginConnector.isPluginInstalled(QsPluginConnector.PLUGIN_NAME)) {
                    return true
                }
                break
            case FxdPluginConnector.PLUGIN_NAME:
                if (PluginConnector.isPluginInstalled(FxdPluginConnector.PLUGIN_NAME)) {
                    return true
                }
                break
            case ExchangeHousePluginConnector.PLUGIN_NAME:
                if (PluginConnector.isPluginInstalled(ExchangeHousePluginConnector.PLUGIN_NAME)) {
                    return true
                }
                break
			case PtPluginConnector.PLUGIN_NAME:
				if (PluginConnector.isPluginInstalled(PtPluginConnector.PLUGIN_NAME)) {
					return true
				}
				break
            case ArmsPluginConnector.PLUGIN_NAME:
                if (PluginConnector.isPluginInstalled(ArmsPluginConnector.PLUGIN_NAME)) {
					return true
				}
				break
            case SarbPluginConnector.PLUGIN_NAME:
                if (PluginConnector.isPluginInstalled(SarbPluginConnector.PLUGIN_NAME)) {
					return true
				}
				break
            case DocumentPluginConnector.PLUGIN_NAME:
                if (PluginConnector.isPluginInstalled(DocumentPluginConnector.PLUGIN_NAME)) {
                    return true
                }
                break
            case DataPipeLinePluginConnector.PLUGIN_NAME:
                if (PluginConnector.isPluginInstalled(DataPipeLinePluginConnector.PLUGIN_NAME)) {
                    return true
                }
                break

            case ELearningPluginConnector.PLUGIN_NAME:
                if (PluginConnector.isPluginInstalled(ELearningPluginConnector.PLUGIN_NAME)) {
                    return true
                }
                break
            default:
                return false
                break
        }
        return false
    }

}

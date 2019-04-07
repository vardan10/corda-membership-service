package com.example.server

import com.r3.businessnetworks.membership.flows.member.RequestMembershipFlow
import com.r3.businessnetworks.membership.flows.bno.ActivateMembershipFlow
import com.r3.businessnetworks.membership.flows.bno.SuspendMembershipFlow
import com.r3.businessnetworks.membership.flows.member.AmendMembershipMetadataFlow
import com.r3.businessnetworks.membership.flows.member.GetMembershipsFlow
import com.r3.businessnetworks.membership.states.MembershipState
import com.r3.businessnetworks.membership.states.MembershipStatus
import net.corda.core.contracts.StateAndRef
import net.corda.core.identity.CordaX500Name
import net.corda.core.identity.Party
import net.corda.core.messaging.startTrackedFlow
import net.corda.core.messaging.vaultQueryBy
import net.corda.core.serialization.CordaSerializable
import net.corda.core.utilities.getOrThrow
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType.*
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletRequest

/**
 *  A Spring Boot Server API controller for interacting with the node via RPC.
 */

@RestController
@RequestMapping("/api/membership/") // The paths for GET and POST requests are relative to this base path.
class MembershipController(rpc: NodeRPCConnection) {

    companion object {
        private val logger = LoggerFactory.getLogger(RestController::class.java)
    }

    private val myLegalName = rpc.proxy.nodeInfo().legalIdentities.first().name
    private val proxy = rpc.proxy


    /**
     * Request for membership
     */
    @PostMapping(value = [ "request" ], produces = [ TEXT_PLAIN_VALUE ], headers = [ "Content-Type=application/x-www-form-urlencoded" ])
    fun requestMembership(request: HttpServletRequest): ResponseEntity<String>  {

        val partyRole = request.getParameter("role")
        val partyName = request.getParameter("name")

        val partyX500Name = CordaX500Name.parse(partyName)
        val otherParty = proxy.wellKnownPartyFromX500Name(partyX500Name) ?: return ResponseEntity.badRequest().body("Party name cannot be found.\n")

//        val memMetaData = SimpleMembershipMetadata(partyRole,partyName)

        return try {
            val signedTx = proxy.startTrackedFlow(::RequestMembershipFlow, otherParty, "role=" + partyRole).returnValue.getOrThrow()
            return ResponseEntity.status(HttpStatus.CREATED).body("Transaction id ${signedTx.id} committed to ledger.\n")
        } catch (ex: Throwable) {
            logger.error(ex.message, ex)
            return ResponseEntity.badRequest().body(ex.message!!)
        }
    }

    /**
     * Request for Activate membership
     */
    @PostMapping(value = [ "activate" ], produces = [ TEXT_PLAIN_VALUE ], headers = [ "Content-Type=application/x-www-form-urlencoded" ])
    fun activateMembership(request: HttpServletRequest): ResponseEntity<String>  {

        val partyName = request.getParameter("name")

        val partyX500Name = CordaX500Name.parse(partyName)
        val otherParty = proxy.wellKnownPartyFromX500Name(partyX500Name) ?: return ResponseEntity.badRequest().body("Party name cannot be found.\n")

        val myious = proxy.vaultQueryBy<MembershipState<*>>().states.filter { it.state.data.member.equals(otherParty) }
                .filter { it.state.data.status == MembershipStatus.PENDING }.first()

        logger.info(myious.state.data.toString())

        return try {
            val signedTx = proxy.startTrackedFlow(::ActivateMembershipFlow, myious).returnValue.getOrThrow()
            return ResponseEntity.status(HttpStatus.CREATED).body("Transaction id ${signedTx.id} committed to ledger.\n")
        } catch (ex: Throwable) {
            logger.error(ex.message, ex)
            return ResponseEntity.badRequest().body(ex.message!!)
        }
    }

    /**
     * Get membership
     */
    @PostMapping(value = [ "get" ], produces = [ APPLICATION_JSON_VALUE ], headers = [ "Content-Type=application/x-www-form-urlencoded" ])
    fun getMembership(request: HttpServletRequest): ResponseEntity<Map<Party, StateAndRef<MembershipState<*>>>>  {

        val partyName = request.getParameter("name")

        val partyX500Name = CordaX500Name.parse(partyName)
        val otherParty = proxy.wellKnownPartyFromX500Name(partyX500Name) ?: return ResponseEntity.badRequest().body(null)

        return try {
            val signedTx = proxy.startTrackedFlow(::GetMembershipsFlow, otherParty, false, true).returnValue.getOrThrow()
            return ResponseEntity.status(HttpStatus.CREATED).body(signedTx)
        } catch (ex: Throwable) {
            logger.error(ex.message, ex)
            return ResponseEntity.badRequest().body(null)
        }
    }

    /**
     * Ammend Membership metadata
     */
    @PostMapping(value = [ "ammend" ], produces = [ TEXT_PLAIN_VALUE ], headers = [ "Content-Type=application/x-www-form-urlencoded" ])
    fun ammendMembership(request: HttpServletRequest): ResponseEntity<String>  {

        val partyRole = request.getParameter("role")
        val partyName = request.getParameter("name")

        val partyX500Name = CordaX500Name.parse(partyName)
        val otherParty = proxy.wellKnownPartyFromX500Name(partyX500Name) ?: return ResponseEntity.badRequest().body("Party name cannot be found.\n")

//        val memMetaData = SimpleMembershipMetadata(partyRole,partyName)

        return try {
            val signedTx = proxy.startTrackedFlow(::AmendMembershipMetadataFlow, otherParty, "role=" + partyRole).returnValue.getOrThrow()
            return ResponseEntity.status(HttpStatus.CREATED).body("Transaction id ${signedTx.id} committed to ledger.\n")
        } catch (ex: Throwable) {
            logger.error(ex.message, ex)
            return ResponseEntity.badRequest().body(ex.message!!)
        }
    }

    /**
     * Revoke Membership
     */
    @PostMapping(value = [ "revoke" ], produces = [ TEXT_PLAIN_VALUE ], headers = [ "Content-Type=application/x-www-form-urlencoded" ])
    fun revokeMembership(request: HttpServletRequest): ResponseEntity<String>  {

        val partyName = request.getParameter("name")

        val partyX500Name = CordaX500Name.parse(partyName)
        val otherParty = proxy.wellKnownPartyFromX500Name(partyX500Name) ?: return ResponseEntity.badRequest().body("Party name cannot be found.\n")

        val myious = proxy.vaultQueryBy<MembershipState<*>>().states.filter { it.state.data.member.equals(otherParty) }.first()

        return try {
            val signedTx = proxy.startTrackedFlow(::SuspendMembershipFlow, myious).returnValue.getOrThrow()
            return ResponseEntity.status(HttpStatus.CREATED).body("Transaction id ${signedTx.id} committed to ledger.\n")
        } catch (ex: Throwable) {
            logger.error(ex.message, ex)
            return ResponseEntity.badRequest().body(ex.message!!)
        }
    }

}

@CordaSerializable
data class SimpleMembershipMetadata(val role: String = "", val displayedName: String = "")